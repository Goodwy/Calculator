package dev.goodwy.math.helpers.converters

import dev.goodwy.math.R
import dev.goodwy.math.helpers.MATH_CONTEXT
import java.math.BigDecimal

// The converter uses L/100km as its base unit, which simplifies all conversions.
// All conversions are based on this base unit, ensuring accuracy and consistency.
object FuelConverter : Converter {
    override val nameResId: Int = R.string.unit_fuel
    override val imageResId: Int = R.drawable.ic_fuel
    override val key: String = "FuelConverter"

    sealed class Unit(nameResId: Int, symbolResId: Int, factor: BigDecimal, key: String) :
        Converter.Unit(nameResId, symbolResId, factor, key) {

        // Miles per gallon (US)
        data object MilesPerGallon : Unit(
            nameResId = R.string.unit_fuel_mpg,
            symbolResId = R.string.unit_fuel_symbol_mpg,
            factor = BigDecimal.ONE,
            key = "MilesPerGallon"
        ) {
            override fun toBase(value: BigDecimal): BigDecimal {
                // Base unit: litres per 100 km
                // 1 MPG = 235.214583 L/100km
                if (value == BigDecimal.ZERO) return BigDecimal.ZERO
                val conversionFactor = BigDecimal("235.214583")
                return conversionFactor.divide(value, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // From litres per 100 km to MPG
                if (value == BigDecimal.ZERO) return BigDecimal.ZERO
                val conversionFactor = BigDecimal("235.214583")
                return conversionFactor.divide(value, MATH_CONTEXT)
            }
        }

        // Miles per gallon (US)
        data object GallonsPerHundredMiles : Unit(
            nameResId = R.string.unit_fuel_gallons_per_hundred_miles,
            symbolResId = R.string.unit_fuel_symbol_gallons_per_hundred_miles,
            factor = BigDecimal.ONE,
            key = "GallonsPerHundredMiles"
        ) {
            override fun toBase(value: BigDecimal): BigDecimal {
                // 1 gal/100 miles = 235.214583 / (100 miles per gallon)
                // But let’s put it more simply: 1 gal/100 miles = 235.214583 / 100 = 2.35214583 L/100 km? No, it’s more complicated than that
                // Formula: L/100 km = (gal/100 miles) * 235.214583 / 100 = gal/100 miles * 2.35214583
                if (value == BigDecimal.ZERO) return BigDecimal.ZERO
                val conversionFactor = BigDecimal("2.35214583")
                return value.multiply(conversionFactor, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // From L/100 km to gal/100 miles
                if (value == BigDecimal.ZERO) return BigDecimal.ZERO
                val conversionFactor = BigDecimal("2.35214583")
                return value.divide(conversionFactor, MATH_CONTEXT)
            }
        }

        // Kilometer per liter
        data object KilometersPerLiter : Unit(
            nameResId = R.string.unit_fuel_km_per_liter,
            symbolResId = R.string.unit_fuel_symbol_km_per_liter,
            factor = BigDecimal.ONE,
            key = "KilometersPerLiter"
        ) {
            override fun toBase(value: BigDecimal): BigDecimal {
                // 1 км/л = 100 / км/л = L/100km
                if (value == BigDecimal.ZERO) return BigDecimal.ZERO
                val hundred = BigDecimal("100")
                return hundred.divide(value, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // Из L/100km в км/л
                if (value == BigDecimal.ZERO) return BigDecimal.ZERO
                val hundred = BigDecimal("100")
                return hundred.divide(value, MATH_CONTEXT)
            }
        }

        // Liters per 100 kilometers (base unit)
        data object LitersPerHundredKilometers : Unit(
            nameResId = R.string.unit_fuel_l_per_100km,
            symbolResId = R.string.unit_fuel_symbol_l_per_100km,
            factor = BigDecimal.ONE,
            key = "LitersPerHundredKilometers"
        ) {
            override fun toBase(value: BigDecimal): BigDecimal = value

            override fun fromBase(value: BigDecimal): BigDecimal = value
        }
    }

    override val units: List<Unit> = listOf(
        Unit.MilesPerGallon,
        Unit.GallonsPerHundredMiles,
        Unit.KilometersPerLiter,
        Unit.LitersPerHundredKilometers,
    )

    override val defaultTopUnit: Unit = Unit.MilesPerGallon
    override val defaultBottomUnit: Unit = Unit.LitersPerHundredKilometers
}
