package com.microserviceutils.googlesheetadapter.service


import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.*
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.microserviceutils.googlesheetadapter.model.ColorDto
import com.microserviceutils.googlesheetadapter.model.DataDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.io.IOException
import kotlin.collections.setOf

@Service
@ConditionalOnProperty(prefix = "google-doc", name = ["service-account-key-base64", "sheet", "application_name",])
class SpreadsheetService {

    @Value("\${google-doc.sheet}")
    private lateinit var SPREADSHEET_ID: String

    companion object {
        @Value("\${google-doc.application_name}")
        private var applicationName: String = ""

        private val service: Sheets by lazy {

            val credentials: GoogleCredentials = try {
                JWTGenerate.credentials.createScoped(setOf(SheetsScopes.SPREADSHEETS))
            } catch (e: IOException) {
                throw RuntimeException("Failed to load credentials", e)
            }

            Sheets.Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                HttpCredentialsAdapter(credentials)
            )
                .setApplicationName(applicationName)
                .build()
        }
    }

    fun updateData(dataDto: DataDto) {
        val valueRange = ValueRange()
        valueRange.setValues(dataDto.data)
        valueRange.setRange(dataDto.range)
        updateDataInTable(valueRange)
    }

    fun getSheetNames(): List<String> {
        val spreadsheet = service.spreadsheets().get(SPREADSHEET_ID).execute()
        return spreadsheet.sheets
            ?.mapNotNull { it.properties?.title }
            ?: emptyList()
    }

    fun getListValues(title: String): List<List<Any>> {
        val response = service.spreadsheets().values()
            .get(SPREADSHEET_ID, title)
            .execute()
        return response.getValues() ?: emptyList()
    }

    fun getAllSheetsValues(): Map<String, List<List<Any>>> {
        val sheetTitles = getSheetNames()
        val result = mutableMapOf<String, List<List<Any>>>()
        for (title in sheetTitles) {
            result[title] = getListValues(title)
        }
        return result
    }


    fun styleRow(sheetId: Int, rowIndex: Int, columnIndex: Int, textColor: ColorDto, cellColor: ColorDto) {
        val request = Request().apply {
            repeatCell = RepeatCellRequest().apply {
                range = GridRange().apply {
                    this.sheetId = sheetId
                    startRowIndex = rowIndex
                    endRowIndex = rowIndex + 1
                    startColumnIndex = columnIndex
                    endColumnIndex = columnIndex + 1
                }
                cell = CellData().apply {
                    userEnteredFormat = CellFormat().apply {
                        backgroundColor = Color().apply {
                            red = cellColor.red
                            green = cellColor.green
                            blue = cellColor.blue
                        }
                        textFormat = TextFormat().apply {
                            bold = true
                            foregroundColor = Color().apply {
                                red = textColor.red
                                green = textColor.green
                                blue = textColor.blue
                            }
                        }
                    }
                    fields = "*"
                }
            }
        }
        val batchUpdateRequest = BatchUpdateSpreadsheetRequest().apply {
            requests = listOf(request)
        }
        try {
            service.spreadsheets().batchUpdate(SPREADSHEET_ID, batchUpdateRequest).execute()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }



    private fun updateDataInTable(valueRange: ValueRange) {
        try {
            service.spreadsheets().values().update(SPREADSHEET_ID, valueRange.range, valueRange)
                .setValueInputOption("RAW")
                .execute()
        } catch (e: IOException) {
            throw java.lang.RuntimeException(e)
        }
    }

}