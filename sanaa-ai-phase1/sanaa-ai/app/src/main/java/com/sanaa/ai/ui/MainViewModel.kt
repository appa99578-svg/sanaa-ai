package com.sanaa.ai.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanaa.ai.ai.GeminiClient
import com.sanaa.ai.settings.DEFAULT_PERSONALITY
import com.sanaa.ai.settings.Prefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AssistantState { IDLE, LISTENING, THINKING, SPEAKING, EXECUTING, ERROR }

data class ChatMsg(val text: String, val fromUser: Boolean, val isError: Boolean = false)

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = Prefs(app)
    private val client = GeminiClient()

    val messages = MutableStateFlow<List<ChatMsg>>(emptyList())
    val state = MutableStateFlow(AssistantState.IDLE)
    val showSettings = MutableStateFlow(false)

    val apiKey = MutableStateFlow(prefs.apiKey)
    val model = MutableStateFlow(prefs.model)
    val personality = MutableStateFlow(prefs.personality)
    val temperature = MutableStateFlow(prefs.temperature)
    val testResult = MutableStateFlow<String?>(null)
    val testing = MutableStateFlow(false)

    fun setApiKey(v: String) { apiKey.value = v; prefs.apiKey = v }
    fun setModel(v: String) { model.value = v; prefs.model = v }
    fun setPersonality(v: String) { personality.value = v; prefs.personality = v }
    fun resetPersonality() = setPersonality(DEFAULT_PERSONALITY)
    fun setTemperature(v: Float) { temperature.value = v; prefs.temperature = v }
    fun openSettings(open: Boolean) { showSettings.value = open }

    fun clearHistory() {
        messages.value = emptyList()
        state.value = AssistantState.IDLE
    }

    fun send(text: String) {
        val t = text.trim()
        if (t.isEmpty() || state.value == AssistantState.THINKING) return
        if (apiKey.value.isBlank()) {
            messages.update { it + ChatMsg("Settings lo Gemini API key enter cheyyandi.", false, true) }
            state.value = AssistantState.ERROR
            return
        }
        messages.update { it + ChatMsg(t, true) }
        state.value = AssistantState.THINKING
        viewModelScope.launch {
            val history = messages.value.filter { !it.isError }.takeLast(20)
            val result = client.chat(apiKey.value, model.value, personality.value, history, temperature.value)
            result.onSuccess { reply ->
                messages.update { it + ChatMsg(reply, false) }
                state.value = AssistantState.IDLE
            }.onFailure { err ->
                messages.update { it + ChatMsg(err.message ?: "Error", false, true) }
                state.value = AssistantState.ERROR
            }
        }
    }

    fun testConnection() {
        if (testing.value) return
        testing.value = true
        testResult.value = null
        viewModelScope.launch {
            val r = client.chat(
                apiKey.value, model.value, "You are a connection test.",
                listOf(ChatMsg("Reply with just: OK", true)), 0f
            )
            testResult.value = r.fold(
                onSuccess = { "Connected. Model replied: ${it.take(60)}" },
                onFailure = { "Failed: ${it.message}" }
            )
            testing.value = false
        }
    }
}
