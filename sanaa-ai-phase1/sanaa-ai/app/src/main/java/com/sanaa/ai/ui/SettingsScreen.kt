package com.sanaa.ai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    vm: MainViewModel, apiKey: String, model: String, personality: String,
    temperature: Float, testResult: String?, testing: Boolean
) {
    Column(
        Modifier.fillMaxSize().background(Amoled).systemBarsPadding()
            .verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            TextButton(onClick = { vm.openSettings(false) }) { Text("< Back", color = NeonPink) }
        }
        Text("AI Brain", style = MaterialTheme.typography.titleLarge, color = NeonPink)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = apiKey, onValueChange = vm::setApiKey,
            label = { Text("Gemini API key") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Key is encrypted with Android Keystore and stored only on this phone.",
            style = MaterialTheme.typography.labelSmall, color = Color(0xFFB9A6D9)
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = model, onValueChange = vm::setModel,
            label = { Text("Gemini model") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row {
            listOf("gemini-2.5-flash", "gemini-2.5-pro", "gemini-2.5-flash-lite").forEach { m ->
                TextButton(onClick = { vm.setModel(m) }) {
                    Text(m.removePrefix("gemini-"), color = NeonPurple)
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { vm.testConnection() }, enabled = !testing && apiKey.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
        ) { Text(if (testing) "Testing..." else "Test API Connection") }
        if (testResult != null) {
            Text(
                testResult, modifier = Modifier.padding(top = 8.dp),
                color = if (testResult.startsWith("Connected")) Color(0xFF7CFFB2) else ErrorRed
            )
        }

        Spacer(Modifier.height(24.dp))
        Text("Response", style = MaterialTheme.typography.titleMedium, color = NeonPink)
        Text("Creativity: ${"%.1f".format(temperature)}", color = Color(0xFFB9A6D9))
        Slider(value = temperature, onValueChange = vm::setTemperature, valueRange = 0f..1.5f)

        Spacer(Modifier.height(16.dp))
        Text("Personality", style = MaterialTheme.typography.titleMedium, color = NeonPink)
        OutlinedTextField(
            value = personality, onValueChange = vm::setPersonality,
            modifier = Modifier.fillMaxWidth(), minLines = 4
        )
        TextButton(onClick = { vm.resetPersonality() }) { Text("Reset to default", color = NeonPurple) }

        Spacer(Modifier.height(24.dp))
        Text("Privacy", style = MaterialTheme.typography.titleMedium, color = NeonPink)
        Text(
            "Chat is kept in memory only in Phase 1 (nothing saved to disk).",
            style = MaterialTheme.typography.labelSmall, color = Color(0xFFB9A6D9)
        )
        OutlinedButton(onClick = { vm.clearHistory() }) {
            Text("Clear conversation history", color = ErrorRed)
        }
        Spacer(Modifier.height(32.dp))
    }
}
