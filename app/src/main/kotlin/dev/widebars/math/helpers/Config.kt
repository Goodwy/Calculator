package dev.goodwy.math.helpers

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.Flow
import com.goodwy.commons.helpers.BaseConfig
import dev.goodwy.math.helpers.converters.Converter
import dev.goodwy.math.models.ConverterUnitsState

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    fun getLastConverterUnits(converter: Converter): ConverterUnitsState? {
        val storedState = prefs.getString("$CONVERTER_UNITS_PREFIX.${converter.key}", null)
        return if (!storedState.isNullOrEmpty()) {
            val parts = storedState.split(",").map { part ->
                converter.units.first { it.key == part }
            }
            if (parts.size == 2) {
                ConverterUnitsState(parts[0], parts[1])
            } else {
                null
            }
        } else {
            null
        }
    }

    fun putLastConverterUnits(
        converter: Converter,
        topUnit: Converter.Unit,
        bottomUnit: Converter.Unit
    ) {
        prefs.edit {
            putString(
                "$CONVERTER_UNITS_PREFIX.${converter.key}",
                "${topUnit.key},${bottomUnit.key}"
            )
        }
    }

    val preventPhoneFromSleepingFlow: Flow<Boolean> = ::preventPhoneFromSleeping.asFlowNonNull()
    val vibrateOnButtonPressFlow: Flow<Boolean> = ::vibrateOnButtonPress.asFlowNonNull()

    var decimalPlacesConverter: Int
        get() = prefs.getInt(DECIMAL_PLACES_CONVERTER, 15)
        set(decimalPlacesConverter) =
            prefs.edit { putInt(DECIMAL_PLACES_CONVERTER, decimalPlacesConverter) }
}
