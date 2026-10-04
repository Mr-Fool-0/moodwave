package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.AppPreferences
import com.example.data.model.JournalEntry
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    prefs: AppPreferences,
    entries: List<JournalEntry>,
    onAudioToggled: (Boolean) -> Unit,
    onVolumeChanged: (Float) -> Unit,
    onAmbienceChanged: (String) -> Unit,
    onRestoreBackup: (List<JournalEntry>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPinSet by remember { mutableStateOf(prefs.isPinSet()) }
    var showPinDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }

    var procAudio by remember { mutableStateOf(prefs.isProceduralAudioEnabled) }
    var volume by remember { mutableFloatStateOf(prefs.audioVolume) }
    var ambience by remember { mutableStateOf(prefs.ambienceType) }

    var dailyReminder by remember { mutableStateOf(prefs.isDailyReminderEnabled) }
    var reminderTime by remember { mutableStateOf(prefs.reminderTime) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Settings",
            fontFamily = FontFamily.Serif,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Privacy & Security Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "PRIVACY & SECURITY",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 1.4.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "4-Digit Passcode Lock",
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isPinSet) "Passcode active" else "Unsecured",
                            fontSize = 12.sp,
                            color = if (isPinSet) ColorHappy else TextMuted
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                newPinInput = ""
                                showPinDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentDefault),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("set_pin_btn")
                        ) {
                            Text(if (isPinSet) "Change" else "Enable", color = BgDark, fontSize = 12.sp)
                        }

                        if (isPinSet) {
                            OutlinedButton(
                                onClick = {
                                    prefs.setPin(null)
                                    isPinSet = false
                                    Toast.makeText(context, "Passcode removed", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                            ) {
                                Text("Remove", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Soundscape & Atmosphere Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "SOUNDSCAPE & ATMOSPHERE",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 1.4.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Procedural Audio Synthesizer",
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Harmonic piano & flute tuned to frequency",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = procAudio,
                        onCheckedChange = {
                            procAudio = it
                            prefs.isProceduralAudioEnabled = it
                            onAudioToggled(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BgDark,
                            checkedTrackColor = AccentDefault
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ambient Sound Generator",
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val ambienceOptions = listOf("none" to "None", "rain" to "Gentle Rain", "ocean" to "Ocean Waves", "wind" to "Night Wind")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ambienceOptions.forEach { (key, label) ->
                        val isSel = ambience == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, if (isSel) AccentDefault else CardBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    ambience = key
                                    prefs.ambienceType = key
                                    onAmbienceChanged(key)
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = if (isSel) AccentDefault else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Volume: ${(volume * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Slider(
                    value = volume,
                    onValueChange = {
                        volume = it
                        prefs.audioVolume = it
                        onVolumeChanged(it)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = AccentDefault,
                        activeTrackColor = AccentDefault
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Reminder Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "DAILY REFLECTION REMINDER",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 1.4.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Evening Gentle Check-in",
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Daily at $reminderTime",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = dailyReminder,
                        onCheckedChange = {
                            dailyReminder = it
                            prefs.isDailyReminderEnabled = it
                            Toast.makeText(context, if (it) "Reminder set for $reminderTime" else "Reminder disabled", Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BgDark,
                            checkedTrackColor = AccentDefault
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Backup Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "LOCAL-FIRST BACKUP",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 1.4.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Export your private journal records as a .moodwave file to preserve your frequency forever.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val exportJson = StringBuilder().apply {
                            append("{\n  \"app\": \"MoodWave\",\n  \"entries\": [\n")
                            entries.forEachIndexed { i, e ->
                                append("    {\"id\": \"${e.id}\", \"title\": \"${e.title}\", \"content\": \"${e.content.replace("\"", "\\\"")}\", \"emotion\": \"${e.dominantEmotion}\", \"intensity\": ${e.intensity}, \"createdAt\": ${e.createdAt}}")
                                if (i < entries.size - 1) append(",")
                                append("\n")
                            }
                            append("  ]\n}")
                        }.toString()

                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "MoodWave_Backup.moodwave")
                            putExtra(Intent.EXTRA_TEXT, exportJson)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Export MoodWave Backup"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentDefault),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("export_backup_btn")
                ) {
                    Text("Export Backup (.moodwave)", color = BgDark, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Set PIN Dialog
    if (showPinDialog) {
        Dialog(onDismissRequest = { showPinDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Set 4-Digit Passcode",
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enter a 4-digit PIN to secure your journal.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4 && it.all { ch -> ch.isDigit() }) newPinInput = it },
                        placeholder = { Text("4 digits", color = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = AccentDefault
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("pin_dialog_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showPinDialog = false }) {
                            Text("Cancel", color = TextMuted)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newPinInput.length == 4) {
                                    prefs.setPin(newPinInput)
                                    isPinSet = true
                                    showPinDialog = false
                                    Toast.makeText(context, "Passcode updated", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = newPinInput.length == 4,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentDefault),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Save PIN", color = BgDark)
                        }
                    }
                }
            }
        }
    }
}
