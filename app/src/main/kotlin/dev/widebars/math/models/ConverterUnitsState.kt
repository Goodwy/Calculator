package dev.goodwy.math.models

import dev.goodwy.math.helpers.converters.Converter

data class ConverterUnitsState(
    val topUnit: Converter.Unit,
    val bottomUnit: Converter.Unit,
)
