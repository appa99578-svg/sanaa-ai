package com.sanaa.ai.settings

import android.content.Context
import com.sanaa.ai.security.SecureStore

const val DEFAULT_PERSONALITY =
    "You are SANAA, a friendly, playful, caring and respectful female AI companion inspired by Jarvis. " +
    "You always make it clear that you are an AI. You understand and speak Telugu, Hindi and English, " +
    "including mixed-language messages; reply in the same language style the user uses " +
    "(Telugu written in English letters is fine). Keep replies short, warm and helpful. " +
    "You can help with coding in Python, Kotlin, Java, JavaScript, HTML, CSS and C/C++."

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("sanaa_prefs", Context.MODE_PRIVATE)

    var apiKey: String
        get() = sp.getString("api_key_enc", null)?.let { SecureStore.decrypt(it) } ?: ""
        set(v) {
            if (v.isBlank()) sp.edit().remove("api_key_enc").apply()
            else sp.edit().putString("api_key_enc", SecureStore.encrypt(v.trim())).apply()
        }

    var model: String
        get() = sp.getString("model", "gemini-2.5-flash") ?: "gemini-2.5-flash"
        set(v) = sp.edit().putString("model", v).apply()

    var personality: String
        get() = sp.getString("personality", DEFAULT_PERSONALITY) ?: DEFAULT_PERSONALITY
        set(v) = sp.edit().putString("personality", v).apply()

    var temperature: Float
        get() = sp.getFloat("temperature", 0.8f)
        set(v) = sp.edit().putFloat("temperature", v).apply()
}
