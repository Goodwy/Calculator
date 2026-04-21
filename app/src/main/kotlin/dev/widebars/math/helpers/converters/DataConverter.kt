package dev.goodwy.math.helpers.converters

import dev.goodwy.math.R
import dev.goodwy.math.helpers.MATH_CONTEXT
import java.math.BigDecimal

object DataConverter : Converter {
    override val nameResId: Int = R.string.unit_data
    override val imageResId: Int = R.drawable.ic_data
    override val key: String = "DataConverter"

    sealed class Unit(nameResId: Int, symbolResId: Int, factor: BigDecimal, key: String) :
        Converter.Unit(nameResId, symbolResId, factor, key) {

        // Bit (базовая единица)
        data object Bit : Unit(
            nameResId = R.string.unit_data_bit,
            symbolResId = R.string.unit_data_symbol_bit,
            factor = BigDecimal.ONE,
            key = "Bit"
        ) {
            override fun toBase(value: BigDecimal): BigDecimal = value
            override fun fromBase(value: BigDecimal): BigDecimal = value
        }

        // Byte (1 byte = 8 bits)
        data object Byte : Unit(
            nameResId = R.string.unit_data_byte,
            symbolResId = R.string.unit_data_symbol_byte,
            factor = BigDecimal.ONE,
            key = "Byte"
        ) {
            private val BYTE_TO_BITS = BigDecimal("8")

            override fun toBase(value: BigDecimal): BigDecimal {
                // Byte -> Bits
                return value.multiply(BYTE_TO_BITS, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // Bits -> Byte
                return value.divide(BYTE_TO_BITS, MATH_CONTEXT)
            }
        }

        // Kibibyte (KiB) - 1024 bytes
        data object Kibibyte : Unit(
            nameResId = R.string.unit_data_kibibyte,
            symbolResId = R.string.unit_data_symbol_kibibyte,
            factor = BigDecimal.ONE,
            key = "Kibibyte"
        ) {
            private val KIBIBYTE_TO_BYTES = BigDecimal("1024")
            private val BYTE_TO_BITS = BigDecimal("8")

            override fun toBase(value: BigDecimal): BigDecimal {
                // KiB -> Bytes -> Bits
                val bytes = value.multiply(KIBIBYTE_TO_BYTES, MATH_CONTEXT)
                return bytes.multiply(BYTE_TO_BITS, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // Bits -> Bytes -> KiB
                val bytes = value.divide(BYTE_TO_BITS, MATH_CONTEXT)
                return bytes.divide(KIBIBYTE_TO_BYTES, MATH_CONTEXT)
            }
        }

        // Kilobyte (kB) - 1000 bytes
        data object Kilobyte : Unit(
            nameResId = R.string.unit_data_kilobyte,
            symbolResId = R.string.unit_data_symbol_kilobyte,
            factor = BigDecimal.ONE,
            key = "Kilobyte"
        ) {
            private val KILOBYTE_TO_BYTES = BigDecimal("1000")
            private val BYTE_TO_BITS = BigDecimal("8")

            override fun toBase(value: BigDecimal): BigDecimal {
                // kB -> Bytes -> Bits
                val bytes = value.multiply(KILOBYTE_TO_BYTES, MATH_CONTEXT)
                return bytes.multiply(BYTE_TO_BITS, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // Bits -> Bytes -> kB
                val bytes = value.divide(BYTE_TO_BITS, MATH_CONTEXT)
                return bytes.divide(KILOBYTE_TO_BYTES, MATH_CONTEXT)
            }
        }

        // Mebibyte (MiB) - 1024 * 1024 bytes
        data object Mebibyte : Unit(
            nameResId = R.string.unit_data_mebibyte,
            symbolResId = R.string.unit_data_symbol_mebibyte,
            factor = BigDecimal.ONE,
            key = "Mebibyte"
        ) {
            private val MEBIBYTE_TO_BYTES = BigDecimal("1048576") // 1024^2
            private val BYTE_TO_BITS = BigDecimal("8")

            override fun toBase(value: BigDecimal): BigDecimal {
                // MiB -> Bytes -> Bits
                val bytes = value.multiply(MEBIBYTE_TO_BYTES, MATH_CONTEXT)
                return bytes.multiply(BYTE_TO_BITS, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // Bits -> Bytes -> MiB
                val bytes = value.divide(BYTE_TO_BITS, MATH_CONTEXT)
                return bytes.divide(MEBIBYTE_TO_BYTES, MATH_CONTEXT)
            }
        }

        // Megabyte (MB) - 1,000,000 bytes
        data object Megabyte : Unit(
            nameResId = R.string.unit_data_megabyte,
            symbolResId = R.string.unit_data_symbol_megabyte,
            factor = BigDecimal.ONE,
            key = "Megabyte"
        ) {
            private val MEGABYTE_TO_BYTES = BigDecimal("1000000") // 1000^2
            private val BYTE_TO_BITS = BigDecimal("8")

            override fun toBase(value: BigDecimal): BigDecimal {
                // MB -> Bytes -> Bits
                val bytes = value.multiply(MEGABYTE_TO_BYTES, MATH_CONTEXT)
                return bytes.multiply(BYTE_TO_BITS, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // Bits -> Bytes -> MB
                val bytes = value.divide(BYTE_TO_BITS, MATH_CONTEXT)
                return bytes.divide(MEGABYTE_TO_BYTES, MATH_CONTEXT)
            }
        }

        // Gibibyte (GiB) - 1024^3 bytes
        data object Gibibyte : Unit(
            nameResId = R.string.unit_data_gibibyte,
            symbolResId = R.string.unit_data_symbol_gibibyte,
            factor = BigDecimal.ONE,
            key = "Gibibyte"
        ) {
            private val GIBIBYTE_TO_BYTES = BigDecimal("1073741824") // 1024^3
            private val BYTE_TO_BITS = BigDecimal("8")

            override fun toBase(value: BigDecimal): BigDecimal {
                // GiB -> Bytes -> Bits
                val bytes = value.multiply(GIBIBYTE_TO_BYTES, MATH_CONTEXT)
                return bytes.multiply(BYTE_TO_BITS, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // Bits -> Bytes -> GiB
                val bytes = value.divide(BYTE_TO_BITS, MATH_CONTEXT)
                return bytes.divide(GIBIBYTE_TO_BYTES, MATH_CONTEXT)
            }
        }

        // Gigabyte (GB) - 1000^3 bytes
        data object Gigabyte : Unit(
            nameResId = R.string.unit_data_gigabyte,
            symbolResId = R.string.unit_data_symbol_gigabyte,
            factor = BigDecimal.ONE,
            key = "Gigabyte"
        ) {
            private val GIGABYTE_TO_BYTES = BigDecimal("1000000000") // 1000^3
            private val BYTE_TO_BITS = BigDecimal("8")

            override fun toBase(value: BigDecimal): BigDecimal {
                // GB -> Bytes -> Bits
                val bytes = value.multiply(GIGABYTE_TO_BYTES, MATH_CONTEXT)
                return bytes.multiply(BYTE_TO_BITS, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // Bits -> Bytes -> GB
                val bytes = value.divide(BYTE_TO_BITS, MATH_CONTEXT)
                return bytes.divide(GIGABYTE_TO_BYTES, MATH_CONTEXT)
            }
        }

        // Tebibyte (TiB) - 1024^4 bytes
        data object Tebibyte : Unit(
            nameResId = R.string.unit_data_tebibyte,
            symbolResId = R.string.unit_data_symbol_tebibyte,
            factor = BigDecimal.ONE,
            key = "Tebibyte"
        ) {
            private val TEBIBYTE_TO_BYTES = BigDecimal("1099511627776") // 1024^4
            private val BYTE_TO_BITS = BigDecimal("8")

            override fun toBase(value: BigDecimal): BigDecimal {
                // TiB -> Bytes -> Bits
                val bytes = value.multiply(TEBIBYTE_TO_BYTES, MATH_CONTEXT)
                return bytes.multiply(BYTE_TO_BITS, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // Bits -> Bytes -> TiB
                val bytes = value.divide(BYTE_TO_BITS, MATH_CONTEXT)
                return bytes.divide(TEBIBYTE_TO_BYTES, MATH_CONTEXT)
            }
        }

        // Terabyte (TB) - 1000^4 bytes
        data object Terabyte : Unit(
            nameResId = R.string.unit_data_terabyte,
            symbolResId = R.string.unit_data_symbol_terabyte,
            factor = BigDecimal.ONE,
            key = "Terabyte"
        ) {
            private val TERABYTE_TO_BYTES = BigDecimal("1000000000000") // 1000^4
            private val BYTE_TO_BITS = BigDecimal("8")

            override fun toBase(value: BigDecimal): BigDecimal {
                // TB -> Bytes -> Bits
                val bytes = value.multiply(TERABYTE_TO_BYTES, MATH_CONTEXT)
                return bytes.multiply(BYTE_TO_BITS, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // Bits -> Bytes -> TB
                val bytes = value.divide(BYTE_TO_BITS, MATH_CONTEXT)
                return bytes.divide(TERABYTE_TO_BYTES, MATH_CONTEXT)
            }
        }

        // Pebibyte (PiB) - 1024^5 bytes
        data object Pebibyte : Unit(
            nameResId = R.string.unit_data_pebibyte,
            symbolResId = R.string.unit_data_symbol_pebibyte,
            factor = BigDecimal.ONE,
            key = "Pebibyte"
        ) {
            private val PEBIBYTE_TO_BYTES = BigDecimal("1125899906842624") // 1024^5
            private val BYTE_TO_BITS = BigDecimal("8")

            override fun toBase(value: BigDecimal): BigDecimal {
                // PiB -> Bytes -> Bits
                val bytes = value.multiply(PEBIBYTE_TO_BYTES, MATH_CONTEXT)
                return bytes.multiply(BYTE_TO_BITS, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // Bits -> Bytes -> PiB
                val bytes = value.divide(BYTE_TO_BITS, MATH_CONTEXT)
                return bytes.divide(PEBIBYTE_TO_BYTES, MATH_CONTEXT)
            }
        }

        // Petabyte (PB) - 1000^5 bytes
        data object Petabyte : Unit(
            nameResId = R.string.unit_data_petabyte,
            symbolResId = R.string.unit_data_symbol_petabyte,
            factor = BigDecimal.ONE,
            key = "Petabyte"
        ) {
            private val PETABYTE_TO_BYTES = BigDecimal("1000000000000000") // 1000^5
            private val BYTE_TO_BITS = BigDecimal("8")

            override fun toBase(value: BigDecimal): BigDecimal {
                // PB -> Bytes -> Bits
                val bytes = value.multiply(PETABYTE_TO_BYTES, MATH_CONTEXT)
                return bytes.multiply(BYTE_TO_BITS, MATH_CONTEXT)
            }

            override fun fromBase(value: BigDecimal): BigDecimal {
                // Bits -> Bytes -> PB
                val bytes = value.divide(BYTE_TO_BITS, MATH_CONTEXT)
                return bytes.divide(PETABYTE_TO_BYTES, MATH_CONTEXT)
            }
        }
    }

    override val units: List<Unit> = listOf(
        Unit.Bit,
        Unit.Byte,
        Unit.Kibibyte,
        Unit.Kilobyte,
        Unit.Mebibyte,
        Unit.Megabyte,
        Unit.Gibibyte,
        Unit.Gigabyte,
        Unit.Tebibyte,
        Unit.Terabyte,
        Unit.Pebibyte,
        Unit.Petabyte,
    )

    override val defaultTopUnit: Unit = Unit.Megabyte
    override val defaultBottomUnit: Unit = Unit.Gigabyte
}
