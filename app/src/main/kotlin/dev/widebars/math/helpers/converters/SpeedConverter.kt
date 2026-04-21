package dev.goodwy.math.helpers.converters

import dev.goodwy.math.R
import dev.goodwy.math.helpers.MATH_CONTEXT
import java.math.BigDecimal

object SpeedConverter : Converter {
    override val nameResId: Int = R.string.unit_speed
    override val imageResId: Int = R.drawable.ic_speed
    override val key: String = "SpeedConverter"

    sealed class Unit(nameResId: Int, symbolResId: Int, factor: BigDecimal, key: String) :
        Converter.Unit(nameResId, symbolResId, factor, key) {

        // Foot per Second (ft/s) - base unit
        data object FootPerSecond : Unit(
            nameResId = R.string.unit_speed_foot_per_second,
            symbolResId = R.string.unit_speed_symbol_foot_per_second,
            factor = BigDecimal.ONE,
            key = "FootPerSecond"
        ) {
            override fun toBase(value: BigDecimal): BigDecimal = value
            override fun fromBase(value: BigDecimal): BigDecimal = value
        }

        // Kilometer per Hour (km/h)
        data object KilometerPerHour : Unit(
            nameResId = R.string.unit_speed_kilometer_per_hour,
            symbolResId = R.string.unit_speed_symbol_kilometer_per_hour,
            factor = BigDecimal.ONE,
            key = "KilometerPerHour"
        ) {
            // 1 km/h = 0.911344 ft/s (1 km/h = 1000 m / 3600 s = 0.2777778 m/s, 1 m/s = 3.28084 ft/s)
            private val CONVERSION_FACTOR = BigDecimal("0.911344415")

            override fun toBase(value: BigDecimal): BigDecimal {
                // km/h -> ft/s
                return value.multiply(CONVERSION_FACTOR, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // ft/s -> km/h
                return value.divide(CONVERSION_FACTOR, MATH_CONTEXT)
            }
        }

        // Knot (kt) - nautical mile per hour
        data object Knot : Unit(
            nameResId = R.string.unit_speed_knot,
            symbolResId = R.string.unit_speed_symbol_knot,
            factor = BigDecimal.ONE,
            key = "Knot"
        ) {
            // 1 knot = 1.68781 ft/s (1 nautical mile = 6076.12 ft, 1 hour = 3600 s)
            private val CONVERSION_FACTOR = BigDecimal("1.687809857")

            override fun toBase(value: BigDecimal): BigDecimal {
                // knot -> ft/s
                return value.multiply(CONVERSION_FACTOR, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // ft/s -> knot
                return value.divide(CONVERSION_FACTOR, MATH_CONTEXT)
            }
        }

        // Meter per Second (m/s)
        data object MeterPerSecond : Unit(
            nameResId = R.string.unit_speed_meter_per_second,
            symbolResId = R.string.unit_speed_symbol_meter_per_second,
            factor = BigDecimal.ONE,
            key = "MeterPerSecond"
        ) {
            // 1 m/s = 3.28084 ft/s
            private val CONVERSION_FACTOR = BigDecimal("3.280839895")

            override fun toBase(value: BigDecimal): BigDecimal {
                // m/s -> ft/s
                return value.multiply(CONVERSION_FACTOR, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // ft/s -> m/s
                return value.divide(CONVERSION_FACTOR, MATH_CONTEXT)
            }
        }

        // Mile per Hour (mi/hr)
        data object MilePerHour : Unit(
            nameResId = R.string.unit_speed_mile_per_hour,
            symbolResId = R.string.unit_speed_symbol_mile_per_hour,
            factor = BigDecimal.ONE,
            key = "MilePerHour"
        ) {
            // 1 mph = 1.46667 ft/s (1 mile = 5280 ft, 1 hour = 3600 s)
            private val CONVERSION_FACTOR = BigDecimal("1.466666667")

            override fun toBase(value: BigDecimal): BigDecimal {
                // mph -> ft/s
                return value.multiply(CONVERSION_FACTOR, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // ft/s -> mph
                return value.divide(CONVERSION_FACTOR, MATH_CONTEXT)
            }
        }
    }

    override val units: List<Unit> = listOf(
        Unit.FootPerSecond,
        Unit.KilometerPerHour,
        Unit.Knot,
        Unit.MeterPerSecond,
        Unit.MilePerHour,
    )

    override val defaultTopUnit: Unit = Unit.KilometerPerHour
    override val defaultBottomUnit: Unit = Unit.MilePerHour
}
