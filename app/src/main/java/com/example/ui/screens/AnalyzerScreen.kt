package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun AnalyzerScreen(
    entries: List<JournalEntry>,
    onCorrectEmotion: (entryId: String, originalEmotion: String, correctedEmotion: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var correctingEntry by remember { mutableStateOf<JournalEntry?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Tone Analyzer",
            fontFamily = FontFamily.Serif,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Review internal readings of your words and correct them to calibrate your lexicon.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (entries.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    val def = EmotionEngine.getDef(entry.dominantEmotion)
                    val dateStr = remember(entry.createdAt) {
                        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(entry.createdAt))
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = dateStr,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 11.sp,
                                letterSpacing = 1.2.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "“${entry.content.take(130)}...”",
                                fontFamily = FontFamily.Serif,
                                fontSize = 15.sp,
                                color = Color(0xFFC6C6CE)
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Column {
                                        Text(
                                            text = def.name,
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 18.sp,
                                            color = def.color
                                        )
                                        Text("Dominant", fontSize = 11.sp, color = TextMuted)
                                    }
                                    Column {
                                        Text(
                                            text = "${entry.intensity}%",
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 18.sp,
                                            color = TextPrimary
                                        )
                                        Text("Intensity", fontSize = 11.sp, color = TextMuted)
                                    }
                                }

                                TextButton(
                                    onClick = { correctingEntry = entry },
                                    modifier = Modifier.testTag("correct_btn_${entry.id}")
                                ) {
                                    Text("That doesn't sound right", fontSize = 12.sp, color = AccentDefault)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No entries to analyze yet.",
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    color = TextSecondary
                )
            }
        }
    }

    // Correction Dialog
    correctingEntry?.let { entry ->
        Dialog(onDismissRequest = { correctingEntry = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Correct this reading",
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "What was the true frequency of this moment?",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    val emotions = EmotionEngine.EMOTIONS.values.toList()
                    val chunked = emotions.chunked(2)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        chunked.forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                pair.forEach { def ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(SurfaceCard)
                                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                            .clickable {
                                                onCorrectEmotion(entry.id, entry.dominantEmotion, def.key)
                                                correctingEntry = null
                                            }
                                            .padding(vertical = 10.dp, horizontal = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = def.name,
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 14.sp,
                                            color = def.color
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { correctingEntry = null },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            }
        }
    }
}
