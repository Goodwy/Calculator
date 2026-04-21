package dev.goodwy.math.activities

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.SeekBar
import android.widget.TextView
import com.goodwy.commons.dialogs.ColorPickerDialog
//import com.goodwy.commons.dialogs.FeatureLockedDialog
import com.goodwy.commons.extensions.adjustAlpha
import com.goodwy.commons.extensions.applyColorFilter
import com.goodwy.commons.extensions.beVisible
import com.goodwy.commons.extensions.getContrastColor
import com.goodwy.commons.extensions.getProperBackgroundColor
import com.goodwy.commons.extensions.getProperPrimaryColor
import com.goodwy.commons.extensions.isDynamicTheme
import com.goodwy.commons.extensions.setFillWithStroke
import com.goodwy.commons.extensions.updateTextColors
import com.goodwy.commons.extensions.viewBinding
import com.goodwy.commons.helpers.IS_CUSTOMIZING_COLORS
import dev.goodwy.math.R
import dev.goodwy.math.databinding.WidgetConfigBinding
import dev.goodwy.math.extensions.config
import dev.goodwy.math.helpers.MyWidgetProvider

class WidgetConfigureActivity : SimpleActivity() {
    private var mBgAlpha = 0f
    private var mWidgetId = 0
    private var mBgColor = 0
    private var mTextColor = 0
    private var mBgColorWithoutTransparency = 0
//    private var mFeatureLockedDialog: FeatureLockedDialog? = null

    private val binding by viewBinding(WidgetConfigBinding::inflate)

    public override fun onCreate(savedInstanceState: Bundle?) {
        useDynamicTheme = false
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(binding.root)
        setupEdgeToEdge(padTopSystem = listOf(binding.configHolder), padBottomSystem = listOf(binding.root))
        initVariables()

        val isCustomizingColors = intent.extras?.getBoolean(IS_CUSTOMIZING_COLORS) ?: false
        mWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (mWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !isCustomizingColors) {
            finish()
        }

        updateTextColors(binding.configWrapper)
        binding.configWrapper.background.applyColorFilter(getProperBackgroundColor())

        binding.configSave.setOnClickListener { saveConfig() }
        binding.configBgColorHolder.setOnClickListener { pickBackgroundColor() }
        binding.configTextColorHolder.setOnClickListener { pickTextColor() }

        val primaryColor = getProperPrimaryColor()
        binding.configBgSeekbar.setColors(mTextColor, primaryColor, primaryColor)
    }

    override fun onResume() {
        super.onResume()
        window.decorView.setBackgroundColor(0)
    }

    private fun initVariables() {
        mBgColor = config.widgetBgColor
        mBgAlpha = Color.alpha(mBgColor) / 255.toFloat()

//        binding.configCalc.viewCalculatorButton.root.beVisible()
        mBgColorWithoutTransparency =
            Color.rgb(Color.red(mBgColor), Color.green(mBgColor), Color.blue(mBgColor))
        binding.configBgSeekbar.setOnSeekBarChangeListener(seekbarChangeListener)
        binding.configBgSeekbar.progress = (mBgAlpha * 100).toInt()
        updateBackgroundColor()

        mTextColor = config.widgetTextColor
        if (mTextColor == resources.getColor(
                com.goodwy.commons.R.color.default_widget_text_color, theme
            ) && isDynamicTheme()
        ) {
            mTextColor = resources.getColor(com.goodwy.commons.R.color.you_primary_color, theme)
        }

        updateTextColor()

        binding.configCalc.formula.text = "2*2"
        binding.configCalc.result.text = "4"
    }

    private fun saveConfig() {
        val appWidgetManager = AppWidgetManager.getInstance(this) ?: return
        val views = RemoteViews(packageName, R.layout.widget).apply {
            applyColorFilter(binding.widgetBackground.id, mBgColor)
        }

        appWidgetManager.updateAppWidget(mWidgetId, views)

        storeWidgetColors()
        requestWidgetUpdate()

        Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId)
            setResult(RESULT_OK, this)
        }
        finish()
    }

    private fun storeWidgetColors() {
        config.apply {
            widgetBgColor = mBgColor
            widgetTextColor = mTextColor
        }
    }

    private fun requestWidgetUpdate() {
        Intent(
            AppWidgetManager.ACTION_APPWIDGET_UPDATE,
            null,
            this,
            MyWidgetProvider::class.java
        ).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(mWidgetId))
            sendBroadcast(this)
        }
    }

    private fun updateBackgroundColor() {
        mBgColor = mBgColorWithoutTransparency.adjustAlpha(mBgAlpha)
        binding.widgetBackground.applyColorFilter(mBgColor)
        binding.configBgColor.setFillWithStroke(mBgColor, mBgColor)
        binding.configSave.backgroundTintList = ColorStateList.valueOf(getProperPrimaryColor())
    }

    private fun updateTextColor() {
        binding.configTextColor.setFillWithStroke(mTextColor, mTextColor)

        binding.configCalc.result.setTextColor(mTextColor)
        binding.configCalc.formula.setTextColor(mTextColor)
        binding.configSave.setTextColor(getProperPrimaryColor().getContrastColor())

        val viewIds = intArrayOf(
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
            R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9,
            R.id.btn_clear, R.id.btn_reset, R.id.btn_decimal, R.id.btn_percent
        )

        viewIds.forEach {
            (findViewById<TextView>(it)).setTextColor(mTextColor)
            (findViewById<TextView>(it)).textSize = resources.getInteger(R.integer.widget_text_size).toFloat()
            (findViewById<TextView>(it)).background = null
        }

        val viewIconIds = intArrayOf(
            R.id.btn_plus, R.id.btn_minus, R.id.btn_multiply,
            R.id.btn_divide, R.id.btn_equals
        )

        viewIconIds.forEach {
            (findViewById<TextView>(it)).setTextColor(mTextColor)
            (findViewById<TextView>(it)).textSize = resources.getInteger(R.integer.widget_text_size_big).toFloat()
            (findViewById<TextView>(it)).background = null
        }

        (findViewById<ImageView>(R.id.btn_convert)).applyColorFilter(mTextColor)
    }

    private fun pickBackgroundColor() {
        ColorPickerDialog(
            this,
            mBgColorWithoutTransparency,
            addDefaultColorButton = true,
            colorDefault = resources.getColor(com.goodwy.commons.R.color.default_widget_bg_color),
            title = resources.getString(com.goodwy.commons.R.string.background_color)
        ) { wasPositivePressed, color, wasDefaultPressed ->
            if (wasPositivePressed || wasDefaultPressed) {
                mBgColorWithoutTransparency = color
                updateBackgroundColor()
            }
        }
    }

    private fun pickTextColor() {
        ColorPickerDialog(
            this,
            mTextColor,
            addDefaultColorButton = true,
            colorDefault = resources.getColor(com.goodwy.commons.R.color.default_widget_text_color),
            title = resources.getString(com.goodwy.commons.R.string.text_color)
        ) { wasPositivePressed, color, wasDefaultPressed ->
            if (wasPositivePressed || wasDefaultPressed) {
                mTextColor = color
                updateTextColor()
            }
        }
    }

    private val seekbarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            mBgAlpha = progress.toFloat() / 100.toFloat()
            updateBackgroundColor()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }
}
