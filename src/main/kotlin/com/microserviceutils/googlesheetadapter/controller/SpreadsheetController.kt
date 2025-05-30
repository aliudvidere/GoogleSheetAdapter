package com.microserviceutils.googlesheetadapter.controller

import com.microserviceutils.googlesheetadapter.constants.EndpointConstants.ALL_SHEETS_VALUES
import com.microserviceutils.googlesheetadapter.constants.EndpointConstants.API
import com.microserviceutils.googlesheetadapter.constants.EndpointConstants.SHEET
import com.microserviceutils.googlesheetadapter.constants.EndpointConstants.SHEET_NAMES
import com.microserviceutils.googlesheetadapter.constants.EndpointConstants.STYLE_BY_RANGE
import com.microserviceutils.googlesheetadapter.constants.EndpointConstants.WRITE_BY_RANGE
import com.microserviceutils.googlesheetadapter.model.DataDto
import com.microserviceutils.googlesheetadapter.model.StyleDto
import com.microserviceutils.googlesheetadapter.service.SpreadsheetService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["${API}${SHEET}"])
class SpreadsheetController(val spreadsheetService: SpreadsheetService) {

    @PostMapping(value = [WRITE_BY_RANGE], produces = [APPLICATION_JSON_VALUE])
    fun writeByRange(@RequestBody dataDto: DataDto) {
        spreadsheetService.updateData(dataDto)
    }

    @GetMapping(value = [SHEET_NAMES], produces = [APPLICATION_JSON_VALUE])
    fun getSheetsNames(): List<String> {
        return spreadsheetService.getSheetNames()
    }

    @GetMapping(value = [ALL_SHEETS_VALUES], produces = [APPLICATION_JSON_VALUE])
    fun getAllSheetsValues(): Map<String, List<List<Any>>> {
        return spreadsheetService.getAllSheetsValues()
    }

    @PostMapping(value = [STYLE_BY_RANGE], produces = [APPLICATION_JSON_VALUE])
    fun styleByRange(@RequestBody styleDto: StyleDto) {
        spreadsheetService.styleRow(styleDto)
    }

}