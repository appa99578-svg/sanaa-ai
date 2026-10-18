package com.sanaa.ai.ai

import com.sanaa.ai.ui.ChatMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class GeminiClient {
    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun chat(
        apiKey: String,
        model: String,
        system: String,
        history: List<ChatMsg>,
        temperature: Float
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val contents = JSONArray()
            history.forEach { m ->
                contents.put(
                    JSONObject()
                        .put("role", if (m.fromUser) "user" else "model")
                        .put("parts", JSONArray().put(JSONObject().put("text", m.text)))
                )
            }
            val body = JSONObject()
                .put(
                    "systemInstruction",
                    JSONObject().put("parts", JSONArray().put(JSONObject().put("text", system)))
                )
                .put("contents", contents)
                .put("generationConfig", JSONObject().put("temperature", temperature.toDouble()))

            val req = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/${model.trim()}:generateContent")
                .header("x-goog-api-key", apiKey.trim())
                .post(body.toString().toRequestBody("application/json".toMediaType()))
                .build()

            http.newCall(req).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) {
                    return@withContext Result.failure(Exception(describe(resp.code, raw)))
                }
                Result.success(parse(raw))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network problem. Internet connection check cheyyandi."))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    private fun parse(raw: String): String {
        val root = JSONObject(raw)
        val cand = root.optJSONArray("candidates")?.optJSONObject(0)
        val parts = cand?.optJSONObject("content")?.optJSONArray("parts")
        val sb = StringBuilder()
        if (parts != null) {
            for (i in 0 until parts.length()) sb.append(parts.getJSONObject(i).optString("text"))
        }
        if (sb.isNotBlank()) return sb.toString().trim()
        val block = root.optJSONObject("promptFeedback")?.optString("blockReason").orEmpty()
        return if (block.isNotEmpty()) "Request blocked by Gemini safety filter ($block)."
        else "Gemini returned an empty reply."
    }

    private fun describe(code: Int, raw: String): String {
        val apiMsg = try {
            JSONObject(raw).optJSONObject("error")?.optString("message").orEmpty()
        } catch (e: Exception) { "" }
        val head = when (code) {
            400 -> "Bad request (check model name / API key)"
            401, 403 -> "API key rejected or not permitted"
            404 -> "Model not found"
            429 -> "Usage limit reached. Konchem aagi try cheyyandi"
            in 500..599 -> "Gemini server problem"
            else -> "HTTP $code"
        }
        return if (apiMsg.isNotBlank()) "$head: $apiMsg" else head
    }
}
