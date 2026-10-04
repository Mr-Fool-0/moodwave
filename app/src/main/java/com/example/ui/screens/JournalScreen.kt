package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.JournalEntry
import com.example.service.AudioRecorderManager
import com.example.service.EmotionAnalysis
import com.example.service.EmotionEngine
import com.example.ui.theme.*

@Composable
fun JournalScreen(
    entries: List<JournalEntry>,
    audioRecorderManager: AudioRecorderManager,
    onSaveEntry: (title: String, content: String, audioPath: String?) -> Unit,
    onEntryClick: (JournalEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isZenMode by remember { mutableStateOf(false) }
    var recordedAudioPath by remember { mutableStateOf<String?>(null) }
    var isRecording by remember { mutableStateOf(false) }

    // Real-time emotion analysis
    val currentAnalysis by remember(content) {
        derivedStateOf {
            if (content.trim().length >= 4) {
                EmotionEngine.analyze(content)
            } else null
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = audioRecorderManager.startRecording()
            if (file != null) {
                isRecording = true
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        if (!isZenMode) {
            Text(
                text = "What is in your thoughts?",
                fontFamily = FontFamily.Serif,
                fontSize = 30.sp,
                fontWeight = FontWeight.Light,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Write freely without editing yourself.",
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Writing Panel
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Title (optional)
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text(
                            text = "Title (optional)",
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp,
                            color = TextMuted
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = AccentDefault
                    ),
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        color = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("journal_title_input")
                )

                HorizontalDivider(color = CardBorder, thickness = 0.8.dp)

                // Main Content
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = {
                        Text(
                            text = "Describe how the day settled on you...",
                            fontFamily = FontFamily.Serif,
                            fontSize = 17.sp,
                            color = TextMuted
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = AccentDefault
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontSize = 17.sp,
                        lineHeight = 26.sp,
                        color = TextPrimary
                    ),
                    minLines = if (isZenMode) 14 else 7,
                    modifier = Modifier.fillMaxWidth().testTag("journal_content_input")
                )

                // Audio Note attachment badge
                if (recordedAudioPath != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22FFFFFF))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = AccentDefault, modifier = Modifier.size(16.dp))
                        Text("Voice note attached", fontSize = 12.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove audio",
                            tint = TextMuted,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { recordedAudioPath = null }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toolbar Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Voice note record toggle
                        IconButton(
                            onClick = {
                                if (isRecording) {
                                    val path = audioRecorderManager.stopRecording()
                                    isRecording = false
                                    if (path != null) recordedAudioPath = path
                                } else {
                                    val hasMic = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasMic) {
                                        val file = audioRecorderManager.startRecording()
                                        if (file != null) isRecording = true
                                    } else {
                                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isRecording) Color(0x33FF6B6B) else Color.Transparent)
                                .testTag("journal_mic_btn")
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = "Voice note",
                                tint = if (isRecording) Color(0xFFFF6B6B) else TextSecondary
                            )
                        }

                        // Zen mode toggle
                        IconButton(
                            onClick = { isZenMode = !isZenMode },
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                imageVector = if (isZenMode) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = "Focus mode",
                                tint = if (isZenMode) AccentDefault else TextSecondary
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (content.isNotBlank() || title.isNotBlank()) {
                            TextButton(
                                onClick = {
                                    title = ""
                                    content = ""
                                    recordedAudioPath = null
                                }
                            ) {
                                Text("Clear", color = TextMuted)
                            }
                        }

                        Button(
                            onClick = {
                                if (content.isNotBlank()) {
                                    onSaveEntry(title.trim(), content.trim(), recordedAudioPath)
                                    title = ""
                                    content = ""
                                    recordedAudioPath = null
                                    isZenMode = false
                                }
                            },
                            enabled = content.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentDefault),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("journal_save_btn")
                        ) {
                            Text("Save Note", color = BgDark, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        // Real-time Emotion Wave Progression Preview
        currentAnalysis?.let { analysis ->
            Spacer(modifier = Modifier.height(20.dp))
            val def = EmotionEngine.getDef(analysis.dominantEmotion)

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FREQUENCY READING",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp,
                            color = TextSecondary
                        )

                        Text(
                            text = "${def.name} · ${analysis.intensity}%",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = def.color
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = analysis.reflection,
                        fontFamily = FontFamily.Serif,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFFC6C6CE)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4-step progression wave visualizer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        analysis.progression.forEachIndexed { idx, step ->
                            val stepDef = EmotionEngine.getDef(step.emotion)
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(stepDef.color.copy(alpha = (step.intensity / 100f).coerceAtLeast(0.3f)))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stepDef.name.take(3),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Notes Section
        if (!isZenMode && entries.isNotEmpty()) {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "RECENT NOTES",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                letterSpacing = 1.4.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))

            entries.take(5).forEach { entry ->
                val def = EmotionEngine.getDef(entry.dominantEmotion)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                        .clickable { onEntryClick(entry) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (entry.title.isNotBlank()) entry.title else "Untitled",
                                fontFamily = FontFamily.Serif,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${def.name} · ${entry.intensity}%",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 11.sp,
                                color = def.color
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = entry.content.take(120) + if (entry.content.length > 120) "..." else "",
                            fontFamily = FontFamily.Serif,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = Color(0xFFC6C6CE)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
