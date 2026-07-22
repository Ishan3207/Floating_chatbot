package com.floatingcompanion.services

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

object OpenAiApiService {
    private val client = HttpClient.newBuilder().build()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun sendMessage(apiKey: String, model: String, message: String): String {
        return withContext(Dispatchers.IO) {
            try {
                if (apiKey.isBlank()) {
                    return@withContext "Please configure your API key in Settings."
                }

                val requestBody = buildJsonObject {
                    put("model", model)
                    putJsonArray("messages") {
                        add(buildJsonObject {
                            put("role", "user")
                            put("content", message)
                        })
                    }
                }.toString()

                val url = if (model.startsWith("claude")) {
                    "https://api.anthropic.com/v1/messages" // Not fully implemented format but placeholder
                } else {
                    "https://api.openai.com/v1/chat/completions"
                }

                val requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))

                if (model.startsWith("claude")) {
                    requestBuilder.header("x-api-key", apiKey)
                    requestBuilder.header("anthropic-version", "2023-06-01")
                } else {
                    requestBuilder.header("Authorization", "Bearer $apiKey")
                }

                val response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())

                if (response.statusCode() in 200..299) {
                    val responseJson = json.parseToJsonElement(response.body()).jsonObject
                    
                    if (model.startsWith("claude")) {
                         // Claude response parsing logic simplified
                         return@withContext responseJson["content"]?.jsonArray?.get(0)?.jsonObject?.get("text")?.jsonPrimitive?.content ?: "Could not parse response"
                    } else {
                         return@withContext responseJson["choices"]?.jsonArray?.get(0)?.jsonObject?.get("message")?.jsonObject?.get("content")?.jsonPrimitive?.content ?: "Could not parse response"
                    }
                } else {
                    "Error: HTTP ${response.statusCode()} - ${response.body()}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Exception: ${e.message}"
            }
        }
    }
}
