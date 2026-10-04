package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
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
import com.example.data.model.TimeCapsule
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CapsulesScreen(
    capsules: List<TimeCapsule>,
    onSealCapsule: (content: String, unlockDateMillis: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var content by remember { mutableStateOf("") }
    var selectedDaysAhead by remember { mutableStateOf(30) }
    val now = remember { System.currentTimeMillis() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Time Capsule",
            fontFamily = FontFamily.Serif,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Seal a thought for a future version of yourself to unlock.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Seal Panel
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "SEAL A LETTER TO THE FUTURE",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 1.4.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = {
                        Text(
                            text = "Write a secret, a hope, or where you stand right now...",
                            fontFamily = FontFamily.Serif,
                            fontSize = 15.sp,
                            color = TextMuted
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0x11FFFFFF),
                        unfocusedContainerColor = Color(0x11FFFFFF),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = AccentDefault
                    ),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("capsule_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "UNLOCK IN",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                val options = listOf(7 to "1 Week", 30 to "1 Month", 90 to "3 Months", 365 to "1 Year")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    options.forEach { (days, label) ->
                        val isSelected = selectedDaysAhead == days
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AccentDefault else Color(0x11FFFFFF))
                                .border(1.dp, if (isSelected) AccentDefault else CardBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedDaysAhead = days }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = if (isSelected) BgDark else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (content.isNotBlank()) {
                            val unlockTime = System.currentTimeMillis() + (selectedDaysAhead.toLong() * 24 * 3600 * 1000)
                            onSealCapsule(content.trim(), unlockTime)
                            content = ""
                        }
                    },
                    enabled = content.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentDefault),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth().testTag("seal_capsule_btn")
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = BgDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Seal Capsule", color = BgDark, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Capsules List
        Text(
            text = "YOUR TIME CAPSULES",
            fontFamily = FontFamily.SansSerif,
            fontSize = 11.sp,
            letterSpacing = 1.4.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (capsules.isNotEmpty()) {
            capsules.forEach { capsule ->
                val isUnlocked = capsule.unlockDateMillis <= now
                val unlockDateStr = remember(capsule.unlockDateMillis) {
                    SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(capsule.unlockDateMillis))
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isUnlocked) ColorHappy else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isUnlocked) "UNSEALED" else "SEALED UNTIL $unlockDateStr",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 11.sp,
                                letterSpacing = 1.2.sp,
                                color = if (isUnlocked) ColorHappy else TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isUnlocked) {
                            Text(
                                text = capsule.content,
                                fontFamily = FontFamily.Serif,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = TextPrimary
                            )
                        } else {
                            Text(
                                text = "🔒 [This thought remains sealed in time until its day arrives]",
                                fontFamily = FontFamily.Serif,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontSize = 14.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No capsules currently sealed. Seal a message to your future self above.",
                        fontFamily = FontFamily.Serif,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
