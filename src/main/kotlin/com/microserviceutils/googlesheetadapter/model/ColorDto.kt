package com.microserviceutils.googlesheetadapter.model;

data class ColorDto(
        val red: Float,
        val green: Float,
        val blue: Float,
) {
        constructor(color: Float) : this(color, color, color)
}
