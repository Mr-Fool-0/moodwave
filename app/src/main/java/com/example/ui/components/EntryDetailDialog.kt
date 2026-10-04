package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.JournalEntry
import com.example.service.EmotionEngine
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EntryDetailDialog(
    entry: JournalEntry,
    isPlayingAudio: Boolean,
    onPlayAudio: (String) -> Unit,
    onStopAudio: () -> Unit,
    onToggleFavorite: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val emotionDef = EmotionEngine.getDef(entry.dominantEmotion)
    val dateString = remember(entry.createdAt) {
        SimpleDateFormat("MMMM d, yyyy · h:mm a", Locale.getDefault()).format(Date(entry.createdAt))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with close and favorite
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateString,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = TextSecondary
                    )

                    Row {
                        IconButton(
                            onClick = { onToggleFavorite(!entry.isFavorite) },
                            modifier = Modifier.testTag("entry_toggle_fav")
                        ) {
                            Icon(
                                imageVector = if (entry.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Favorite",
                                tint = if (entry.isFavorite) Color(0xFFFFD166) else TextMuted
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("entry_close_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }
                }

                if (entry.title.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = entry.title,
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Emotion Pill
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(emotionDef.color.copy(alpha = 0.15f))
                            .border(1.dp, emotionDef.color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${emotionDef.name} · ${entry.intensity}%",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = emotionDef.color
                        )
                    }

                    if (entry.sentiment != "neutral") {
                        Text(
                            text = "Sentiment: ${entry.sentiment.capitalize(Locale.ROOT)}",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Journal Text Content
                Text(
                    text = entry.content,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    lineHeight = 26.sp,
                    color = Color(0xFFD4D4DC)
                )

                // Voice Note Playback
                if (!entry.audioPath.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (isPlayingAudio) onStopAudio() else onPlayAudio(entry.audioPath)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("play_audio_btn")
                    ) {
                        Icon(
                            imageVector = if (isPlayingAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = AccentDefault
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPlayingAudio) "Stop Voice Note" else "Listen to Voice Note",
                            color = TextPrimary
                        )
                    }
                }

                // Cues & Themes
                if (entry.themes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "THEMES",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.themes,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Actions: Delete & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFF6B6B)),
                        modifier = Modifier.testTag("entry_delete_btn")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete Entry")
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentDefault),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Done", color = BgDark, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
