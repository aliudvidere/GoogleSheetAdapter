package com.microserviceutils.googlesheetadapter.model;

data class DataDto(
        val data: List<List<String>>,
        val range: String,
)
