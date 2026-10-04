package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.service.EmotionDef
import kotlin.math.sin

@Composable
fun WaveBackgroundCanvas(
    emotionDef: EmotionDef,
    intensity: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveAnimation")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f * 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (18000 / emotionDef.speed).toInt().coerceIn(6000, 36000), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    val waveColor = emotionDef.color
    val ampFactor = (intensity / 100f).coerceIn(0.2f, 1.2f)

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Deep room background
        drawRect(color = Color(0xFF07080C))

        // Ambient radial glow behind the wave
        val glowCenter = Offset(width * 0.5f, height * 0.65f)
        val glowRadius = width.coerceAtLeast(height) * 0.55f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    waveColor.copy(alpha = 0.12f * ampFactor),
                    Color.Transparent
                ),
                center = glowCenter,
                radius = glowRadius
            ),
            center = glowCenter,
            radius = glowRadius
        )

        // Draw 4 harmonic sine wave layers
        val waveStep = 12f
        for (i in 0 until 4) {
            val baseRatio = 0.65f + (i * 0.06f)
            val baseHeight = height * baseRatio
            val layerAmp = height * 0.045f * emotionDef.amplitude * ampFactor * (1f - i * 0.12f)
            val path = Path()

            var x = 0f
            var first = true
            while (x <= width + waveStep) {
                val u = x / width
                var s = sin(u * 6.283f * (1.1f + i * 0.25f) + time * (0.8f + i * 0.25f) + i * 1.5f)
                if (emotionDef.turbulence > 0f) {
                    s += emotionDef.turbulence * sin(u * 32f + time * 2.5f) * 0.35f
                }
                val y = baseHeight + (layerAmp * s)

                if (first) {
                    path.moveTo(x, y)
                    first = false
                } else {
                    path.lineTo(x, y)
                }
                x += waveStep
            }

            val layerAlpha = (0.50f - (i * 0.10f)).coerceAtLeast(0.12f)
            drawPath(
                path = path,
                color = waveColor.copy(alpha = layerAlpha),
                style = Stroke(width = (2.2f - i * 0.3f).coerceAtLeast(1.2f))
            )
        }
    }
}
