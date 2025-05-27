package com.microserviceutils.googlesheetadapter.service

import com.google.auth.oauth2.ServiceAccountCredentials
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import java.io.ByteArrayInputStream
import java.util.*


class JWTGenerate(
    @Value("\${google-doc.service-account-key-base64}")
    private val base64Key: String
) {

    companion object {
        lateinit var credentials: ServiceAccountCredentials
            private set
    }

    @PostConstruct
    fun init() {
        val decoded = Base64.getDecoder().decode(base64Key)
        val inputStream = ByteArrayInputStream(decoded)
        credentials = ServiceAccountCredentials.fromStream(inputStream)
    }
}