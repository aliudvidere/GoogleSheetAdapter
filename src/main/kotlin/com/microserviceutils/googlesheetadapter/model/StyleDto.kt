package com.microserviceutils.googlesheetadapter.model;

data class StyleDto (
    val sheetId: Int,
    val range: List<List<Int>>,
    val textColor: ColorDto,
    val cellColor: ColorDto,
) {
    constructor(range: List<List<Int>>): this(0, range, ColorDto(0f), ColorDto(1f))
    constructor(): this(0, listOf(), ColorDto(0f), ColorDto(1f))
}
