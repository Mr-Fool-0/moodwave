package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JournalEntry
import com.example.service.EmotionEngine
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    entries: List<JournalEntry>,
    streakCount: Int = 0,
    onNavigateToJournal: () -> Unit,
    onNavigateToCheckIn: () -> Unit,
    onEntryClick: (JournalEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val latestEntry = entries.firstOrNull()
    val dominantEmotionKey = latestEntry?.dominantEmotion ?: "calm"
    val emotionDef = EmotionEngine.getDef(dominantEmotionKey)
    val avgIntensity = if (entries.isEmpty()) 45 else entries.take(3).map { it.intensity }.average().toInt()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Headline with Streak Badge in Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Your emotions have a frequency.",
                    fontFamily = FontFamily.Serif,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light,
                    lineHeight = 34.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = emotionDef.toneLine,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Journaling Streak Header Badge
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SurfaceCard,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (streakCount > 0) Color(0xFFFFB703).copy(alpha = 0.6f) else CardBorder
                ),
                modifier = Modifier.testTag("home_streak_badge")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Journaling Streak",
                        tint = if (streakCount > 0) Color(0xFFFFB703) else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "$streakCount",
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (streakCount > 0) Color(0xFFFFB703) else TextMuted
                        )
                        Text(
                            text = if (streakCount == 1) "day streak" else "days streak",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 9.sp,
                            letterSpacing = 0.4.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Current State Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "CURRENT STATE",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = emotionDef.name,
                    fontFamily = FontFamily.Serif,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Light,
                    color = emotionDef.color
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    Column {
                        Text(
                            text = "$avgIntensity%",
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Intensity",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Column {
                        Text(
                            text = "${entries.size}",
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Moments",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Column {
                        Text(
                            text = "$streakCount",
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp,
                            color = if (streakCount > 0) Color(0xFFFFB703) else TextPrimary
                        )
                        Text(
                            text = "Day Streak",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = emotionDef.description,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFFD4D4DC)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onNavigateToJournal,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentDefault),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f).testTag("home_write_btn")
                    ) {
                        Icon(Icons.Default.Create, contentDescription = null, tint = BgDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Write Note", color = BgDark, fontWeight = FontWeight.Medium)
                    }

                    OutlinedButton(
                        onClick = onNavigateToCheckIn,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.weight(1f).testTag("home_checkin_btn")
                    ) {
                        Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("10s Check-in", color = TextPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Daily Poetic Quote
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "DAILY REFLECTION",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 1.4.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "“${emotionDef.poeticQuote}”",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Latest Entry Section
        Text(
            text = "LATEST ENTRY",
            fontFamily = FontFamily.SansSerif,
            fontSize = 11.sp,
            letterSpacing = 1.4.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (latestEntry != null) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .clickable { onEntryClick(latestEntry) }
                    .testTag("latest_entry_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    val dateFormatted = remember(latestEntry.createdAt) {
                        SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(latestEntry.createdAt))
                    }
                    Text(
                        text = dateFormatted,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    if (latestEntry.title.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = latestEntry.title,
                            fontFamily = FontFamily.Serif,
                            fontSize = 19.sp,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = latestEntry.content.take(160) + if (latestEntry.content.length > 160) "..." else "",
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFFC6C6CE)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(emotionDef.color.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${emotionDef.name} · ${latestEntry.intensity}%",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            color = emotionDef.color
                        )
                    }
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your journal is quiet right now. Write your first thought to begin tracing your wave.",
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
