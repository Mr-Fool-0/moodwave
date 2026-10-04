package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ExercisesScreen(modifier: Modifier = Modifier) {
    var isBreathingActive by remember { mutableStateOf(false) }
    var currentSensoryStep by remember { mutableStateOf(0) }

    // Box breathing cycle: 0 = Inhale (4s), 1 = Hold (4s), 2 = Exhale (4s), 3 = Rest (4s)
    var breathPhase by remember { mutableStateOf(0) }

    LaunchedEffect(isBreathingActive) {
        if (isBreathingActive) {
            while (true) {
                breathPhase = 0
                delay(4000)
                breathPhase = 1
                delay(4000)
                breathPhase = 2
                delay(4000)
                breathPhase = 3
                delay(4000)
            }
        }
    }

    val breathScale by animateFloatAsState(
        targetValue = when (breathPhase) {
            0, 1 -> 1.15f
            else -> 0.65f
        },
        animationSpec = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
        label = "BreathScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Guided Grounding",
            fontFamily = FontFamily.Serif,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Gentle, quiet exercises to settle the nervous system.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Box Breathing Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BOX BREATHING",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 1.4.sp,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Pulsating orb
                Box(
                    modifier = Modifier
                        .size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(if (isBreathingActive) breathScale else 0.8f)
                            .clip(CircleShape)
                            .background(ColorCalm.copy(alpha = 0.12f))
                            .border(1.5.dp, ColorCalm.copy(alpha = 0.6f), CircleShape)
                    )

                    val label = when (breathPhase) {
                        0 -> "Inhale..."
                        1 -> "Hold..."
                        2 -> "Exhale slowly..."
                        else -> "Rest..."
                    }

                    Text(
                        text = if (isBreathingActive) label else "Begin",
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { isBreathingActive = !isBreathingActive },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isBreathingActive) Color(0x33FFFFFF) else AccentDefault),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("toggle_breathing_btn")
                ) {
                    Text(
                        text = if (isBreathingActive) "Pause Breathing" else "Start 4x4 Rhythm",
                        color = if (isBreathingActive) TextPrimary else BgDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5-4-3-2-1 Sensory Grounding
        val sensorySteps = listOf(
            "5 Things you can see" to "Look around softly. Notice five objects, textures, or shadows near you.",
            "4 Things you can touch" to "Notice four physical textures: the fabric of your sleeve, the surface of your screen, your feet on the ground.",
            "3 Things you can hear" to "Listen past the immediate room. A distant hum, your own breath, quiet air.",
            "2 Things you can smell" to "Notice two scents in the air or the clean neutral scent of the room.",
            "1 Thing you can taste" to "Notice the subtle lingering taste on your tongue. Bring your attention fully to right now."
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "5-4-3-2-1 SENSORY GROUNDING",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 1.4.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                val (stepTitle, stepDesc) = sensorySteps[currentSensoryStep]

                Text(
                    text = stepTitle,
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp,
                    color = AccentDefault
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stepDesc,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFFC6C6CE)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Step ${currentSensoryStep + 1} of 5",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Button(
                        onClick = {
                            currentSensoryStep = (currentSensoryStep + 1) % sensorySteps.size
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FFFFFF)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("sensory_next_btn")
                    ) {
                        Text(
                            text = if (currentSensoryStep == 4) "Complete" else "Next Step",
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
