package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JournalEntry
import com.example.service.EmotionEngine
import com.example.ui.theme.*

@Composable
fun ReviewsScreen(
    entries: List<JournalEntry>,
    modifier: Modifier = Modifier
) {
    val count = entries.size
    val dominantOverall = remember(entries) {
        if (entries.isEmpty()) "calm" else {
            entries.groupingBy { it.dominantEmotion }.eachCount().maxByOrNull { it.value }?.key ?: "calm"
        }
    }
    val def = EmotionEngine.getDef(dominantOverall)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Your Time in Waves",
            fontFamily = FontFamily.Serif,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Reflections on how your thoughts moved over recent cycles.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Cycle Summary Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "CYCLE SUMMARY",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 1.4.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (count > 0) {
                        "Across $count recorded moments, your dominant frequency returned to ${def.name.lowercase()}. Your writing reflects cycles of movement rather than stasis."
                    } else {
                        "Record your initial thoughts to uncover the rhythm of your week in waves."
                    },
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    lineHeight = 26.sp,
                    color = Color(0xFFD4D4DC)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // What appears on lighter days
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "WHAT APPEARS ON LIGHTER DAYS",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    color = ColorHappy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Unstructured morning time, ambient music, deep conversations with close friends, and walking without a destination.",
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFFC6C6CE)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // What appears on heavier days
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "WHAT APPEARS ON HEAVIER DAYS",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    color = ColorSad
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Multiple overlapping deadlines, fragmented late-night screen time, and hurried physical environments.",
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFFC6C6CE)
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
