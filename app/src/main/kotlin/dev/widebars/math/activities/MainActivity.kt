package dev.goodwy.math.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import me.grantland.widget.AutofitHelper
import com.goodwy.commons.extensions.appLaunched
import com.goodwy.commons.extensions.checkWhatsNew
import com.goodwy.commons.extensions.copyToClipboard
import com.goodwy.commons.extensions.getProperPrimaryColor
import com.goodwy.commons.extensions.getProperTextColor
import com.goodwy.commons.extensions.getSurfaceColor
import com.goodwy.commons.extensions.hideKeyboard
import com.goodwy.commons.extensions.performHapticFeedback
import com.goodwy.commons.extensions.toast
import com.goodwy.commons.extensions.value
import com.goodwy.commons.extensions.viewBinding
import com.goodwy.commons.helpers.APP_ICON_IDS
import com.goodwy.commons.helpers.LICENSE_AUTOFITTEXTVIEW
import com.goodwy.commons.helpers.LICENSE_EVALEX
import com.goodwy.commons.helpers.MEDIUM_ALPHA_INT
import com.goodwy.commons.models.FAQItem
import dev.goodwy.math.BuildConfig
import dev.goodwy.math.R
import dev.goodwy.math.databases.CalculatorDatabase
import dev.goodwy.math.databinding.ActivityMainBinding
import dev.goodwy.math.dialogs.HistoryDialog
import dev.goodwy.math.extensions.config
import dev.goodwy.math.extensions.launchAbout
import dev.goodwy.math.extensions.updateViewColors
import dev.goodwy.math.helpers.CALCULATOR_STATE
import dev.goodwy.math.helpers.Calculator
import dev.goodwy.math.helpers.CalculatorImpl
import dev.goodwy.math.helpers.DIVIDE
import dev.goodwy.math.helpers.HistoryHelper
import dev.goodwy.math.helpers.MINUS
import dev.goodwy.math.helpers.MULTIPLY
import dev.goodwy.math.helpers.PERCENT
import dev.goodwy.math.helpers.PLUS
import dev.goodwy.math.helpers.POWER
import dev.goodwy.math.helpers.ROOT
import dev.goodwy.math.helpers.getDecimalSeparator
import dev.goodwy.math.helpers.whatsNewList

class MainActivity : SimpleActivity(), Calculator {
    private var storedTextColor = 0
    private var vibrateOnButtonPress = true
    private var saveCalculatorState: String = ""
    private lateinit var calc: CalculatorImpl

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        useOverflowIcon = false
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        appLaunched(BuildConfig.APPLICATION_ID)
        setupOptionsMenu()
        setupEdgeToEdge(padBottomSystem = listOf(binding.mainNestedScrollview))
        setupMaterialScrollListener(binding.mainNestedScrollview, binding.mainAppbar)

        if (savedInstanceState != null) {
            saveCalculatorState = savedInstanceState.getCharSequence(CALCULATOR_STATE) as String
        }

        calc = CalculatorImpl(
            calculator = this,
            context = applicationContext,
            calculatorState = saveCalculatorState
        )
        binding.btnPlus?.setOnClickOperation(PLUS)
        binding.btnMinus?.setOnClickOperation(MINUS)
        binding.btnMultiply?.setOnClickOperation(MULTIPLY)
        binding.btnDivide?.setOnClickOperation(DIVIDE)
        binding.btnPercent?.setOnClickOperation(PERCENT)
        binding.btnPower?.setOnClickOperation(POWER)
        binding.btnRoot?.setOnClickOperation(ROOT)
        binding.btnRoot?.setOnLongClickListener {
            calc.handleOperation(POWER)
            true
        }
        binding.btnMinus?.setOnLongClickListener { calc.turnToNegative() }
        binding.btnClear?.setVibratingOnClickListener { calc.handleClear() }
        binding.btnClear?.setOnLongClickListener {
            calc.handleReset()
            true
        }

        getButtonIds().forEach {
            it?.setVibratingOnClickListener { view ->
                calc.numpadClicked(view.id)
            }
        }

        binding.btnEquals?.setVibratingOnClickListener { calc.handleEquals() }
        binding.formula?.setOnLongClickListener { copyToClipboard(false) }
        binding.result?.setOnLongClickListener { copyToClipboard(true) }
        binding.btnConvert?.setVibratingOnClickListener { view ->
            launchUnitConverter()
        }
        AutofitHelper.create(binding.result)
        AutofitHelper.create(binding.formula)
        storeStateVariables()
        binding.calculatorHolder?.let { updateViewColors(it, getProperTextColor()) }
        setupDecimalButton()
        checkAppOnSDCard()

        checkWhatsNewDialog()
    }

    override fun onResume() {
        super.onResume()
        setupTopAppBar(binding.mainAppbar)
        setupMaterialScrollListener(binding.mainNestedScrollview, binding.mainAppbar)
        if (storedTextColor != config.textColor) {
            binding.calculatorHolder?.let { updateViewColors(it, getProperTextColor()) }
        }

        if (config.preventPhoneFromSleeping) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        setupDecimalButton()
        vibrateOnButtonPress = config.vibrateOnButtonPress

        binding.apply {
            val primaryColor = getProperPrimaryColor()
            arrayOf(
                btnMultiply, btnPlus, btnMinus, btnEquals, btnDivide
            ).forEach {
                it?.background = ResourcesCompat.getDrawable(
                    resources, R.drawable.pill_background, theme
                )?.mutate()
                it?.background?.setTint(primaryColor)
//                it?.background?.alpha = MEDIUM_ALPHA_INT
            }

            arrayOf(btnClear, btnReset, btnPower, btnRoot, btnPercent
            ).forEach {
                it?.background = ResourcesCompat.getDrawable(
                    resources, R.drawable.pill_background, theme
                )?.mutate()
                it?.background?.setTint(primaryColor)
                it?.background?.alpha = MEDIUM_ALPHA_INT //LOWER_ALPHA_INT
            }

            val surfaceColor = getSurfaceColor()
            arrayOf(btnConvert, btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9,
                btnDecimal
            ).forEach {
                it?.background = ResourcesCompat.getDrawable(
                    resources, R.drawable.pill_background, theme
                )?.mutate()
                it?.background?.setTint(surfaceColor)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        storeStateVariables()
        if (config.preventPhoneFromSleeping) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            CalculatorDatabase.destroyInstance()
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putString(CALCULATOR_STATE, calc.getCalculatorStateJson().toString())
    }

    private fun setupOptionsMenu() {
        binding.mainToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.history -> showHistory()
                R.id.unit_converter -> launchUnitConverter()
                R.id.settings -> launchSettings()
                R.id.about -> launchAbout()
                else -> return@setOnMenuItemClickListener false
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun storeStateVariables() {
        config.apply {
            storedTextColor = textColor
        }
    }

    private fun checkHaptic(view: View) {
        if (vibrateOnButtonPress) {
            view.performHapticFeedback()
        }
    }

    private fun showHistory() {
        HistoryHelper(this).getHistory {
            if (it.isEmpty()) {
                toast(R.string.history_empty)
            } else {
                HistoryDialog(this, it, calc)
            }
        }
    }

    private fun launchUnitConverter() {
        hideKeyboard()
        startActivity(Intent(applicationContext, UnitConverterPickerActivity::class.java))
    }

    private fun launchSettings() {
        hideKeyboard()
        startActivity(
            Intent(applicationContext, SettingsActivity::class.java).apply {
                putIntegerArrayListExtra(APP_ICON_IDS, getAppIconIDs())
            }
        )
    }

    private fun getButtonIds() = binding.run {
        arrayOf(btnDecimal, btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9)
    }

    private fun copyToClipboard(copyResult: Boolean): Boolean {
        var value = binding.formula?.value
        if (copyResult) {
            value = binding.result?.value
        }

        return if (value.isNullOrEmpty()) {
            false
        } else {
            copyToClipboard(value)
            true
        }
    }

    override fun showNewResult(value: String, context: Context) {
        binding.result?.text = value
    }

    override fun showNewFormula(value: String, context: Context) {
        binding.formula?.text = value
    }

    private fun setupDecimalButton() {
        binding.btnDecimal?.text = getDecimalSeparator()
    }

    private fun View.setVibratingOnClickListener(callback: (view: View) -> Unit) {
        setOnClickListener {
            callback(it)
            checkHaptic(it)
        }
    }

    private fun View.setOnClickOperation(operation: String) {
        setVibratingOnClickListener {
            calc.handleOperation(operation)
        }
    }

    private fun checkWhatsNewDialog() {
        whatsNewList().apply {
            checkWhatsNew(this, BuildConfig.VERSION_CODE)
        }
    }
}
