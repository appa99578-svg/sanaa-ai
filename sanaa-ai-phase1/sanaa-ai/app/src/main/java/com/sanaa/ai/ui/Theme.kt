package com.sanaa.ai.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NeonPink = Color(0xFFFF2E9A)
val NeonPurple = Color(0xFF8A2BE2)
val ErrorRed = Color(0xFFFF4D5A)
val Amoled = Color(0xFF000000)
val Panel = Color(0xFF14101C)

private val scheme = darkColorScheme(
    primary = NeonPink,
    secondary = NeonPurple,
    background = Amoled,
    surface = Panel,
    onPrimary = Color.White,
    onBackground = Color(0xFFF2E9FF),
    onSurface = Color(0xFFF2E9FF),
    error = ErrorRed
)

@Composable
fun SanaaTheme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = scheme, content = content)
