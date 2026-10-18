package com.sanaa.ai.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SanaaOrb(state: AssistantState, size: Dp = 120.dp, modifier: Modifier = Modifier) {
    val period = when (state) {
        AssistantState.IDLE -> 3200
        AssistantState.LISTENING -> 1200
        AssistantState.THINKING -> 700
        AssistantState.SPEAKING -> 900
        AssistantState.EXECUTING -> 800
        AssistantState.ERROR -> 2200
    }
    val transition = rememberInfiniteTransition(label = "orb")
    val pulse by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(period, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val inner = if (state == AssistantState.ERROR) ErrorRed else NeonPink
    val outer = if (state == AssistantState.ERROR) Color(0xFF7A0010) else NeonPurple

    Canvas(modifier.size(size)) {
        val c = Offset(this.size.width / 2f, this.size.height / 2f)
        val base = this.size.minDimension / 2f
        val glow = base * (0.80f + 0.20f * pulse)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(outer.copy(alpha = 0.55f), outer.copy(alpha = 0f)), center = c, radius = base
            ),
            radius = base, center = c
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color.White.copy(alpha = 0.9f), inner, outer, Color.Transparent),
                center = c, radius = glow * 0.75f
            ),
            radius = glow * 0.7f, center = c
        )
    }
}
