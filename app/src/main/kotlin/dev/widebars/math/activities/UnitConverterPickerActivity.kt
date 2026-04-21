package dev.goodwy.math.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.goodwy.commons.extensions.viewBinding
import com.goodwy.commons.helpers.NavigationIcon
import com.goodwy.commons.views.AutoGridLayoutManager
import dev.goodwy.math.R
import dev.goodwy.math.adapters.UnitTypesAdapter
import dev.goodwy.math.databinding.ActivityUnitConverterPickerBinding
import dev.goodwy.math.extensions.config
import dev.goodwy.math.helpers.converters.Converter

class UnitConverterPickerActivity : SimpleActivity() {
    private val binding by viewBinding(ActivityUnitConverterPickerBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupEdgeToEdge(padBottomSystem = listOf(binding.unitTypesGrid))
        setupMaterialScrollListener(
            binding.unitTypesGrid,
            binding.unitConverterPickerAppbar
        )

        binding.unitTypesGrid.layoutManager =
            AutoGridLayoutManager(this, resources.getDimensionPixelSize(R.dimen.unit_type_size))
        binding.unitTypesGrid.adapter = UnitTypesAdapter(this, Converter.ALL) {
            Intent(this, UnitConverterActivity::class.java).apply {
                putExtra(UnitConverterActivity.EXTRA_CONVERTER_ID, it)
                startActivity(this)
            }
        }

        binding.unitConverterPickerToolbar.setTitle(R.string.unit_converter)
    }

    override fun onResume() {
        super.onResume()

        setupTopAppBar(binding.unitConverterPickerAppbar, NavigationIcon.Arrow)

        if (config.preventPhoneFromSleeping) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onPause() {
        super.onPause()
        if (config.preventPhoneFromSleeping) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
