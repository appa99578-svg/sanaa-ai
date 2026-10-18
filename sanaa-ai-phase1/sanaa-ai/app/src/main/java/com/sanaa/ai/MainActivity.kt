package com.sanaa.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanaa.ai.ui.ChatScreen
import com.sanaa.ai.ui.MainViewModel
import com.sanaa.ai.ui.SanaaTheme
import com.sanaa.ai.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SanaaTheme {
                val showSettings by vm.showSettings.collectAsStateWithLifecycle()
                val messages by vm.messages.collectAsStateWithLifecycle()
                val state by vm.state.collectAsStateWithLifecycle()
                BackHandler(enabled = showSettings) { vm.openSettings(false) }
                if (showSettings) {
                    val apiKey by vm.apiKey.collectAsStateWithLifecycle()
                    val model by vm.model.collectAsStateWithLifecycle()
                    val personality by vm.personality.collectAsStateWithLifecycle()
                    val temperature by vm.temperature.collectAsStateWithLifecycle()
                    val testResult by vm.testResult.collectAsStateWithLifecycle()
                    val testing by vm.testing.collectAsStateWithLifecycle()
                    SettingsScreen(vm, apiKey, model, personality, temperature, testResult, testing)
                } else {
                    ChatScreen(messages, state, vm::send) { vm.openSettings(true) }
                }
            }
        }
    }
}
