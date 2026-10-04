package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.EmotionEngine
import com.example.ui.theme.*

@Composable
fun CheckInScreen(
    onSaveCheckIn: (emotion: String, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedEmotion by remember { mutableStateOf<String?>(null) }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "How was today?",
            fontFamily = FontFamily.Serif,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "A 10-second check-in to trace your continuous state.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "CHOOSE YOUR FREQUENCY",
            fontFamily = FontFamily.SansSerif,
            fontSize = 11.sp,
            letterSpacing = 1.4.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Grid of 10 Emotion Pills
        val emotionList = remember { EmotionEngine.EMOTIONS.values.toList() }
        val chunked = remember { emotionList.chunked(2) }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            chunked.forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pair.forEach { def ->
                        val isSelected = selectedEmotion == def.key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) def.color.copy(alpha = 0.22f) else SurfaceCard)
                                .border(
                                    1.dp,
                                    if (isSelected) def.color else CardBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedEmotion = def.key }
                                .padding(vertical = 14.dp, horizontal = 16.dp)
                                .testTag("checkin_pill_${def.key}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = def.name,
                                fontFamily = FontFamily.Serif,
                                fontSize = 16.sp,
                                color = if (isSelected) def.color else TextPrimary
                            )
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedEmotion != null) {
            val def = EmotionEngine.getDef(selectedEmotion!!)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = def.description,
                        fontFamily = FontFamily.Serif,
                        fontSize = 14.sp,
                        color = Color(0xFFC6C6CE)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    TextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = {
                            Text(
                                text = "A single phrase or sentence is enough (optional)...",
                                fontFamily = FontFamily.Serif,
                                fontSize = 14.sp,
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
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("checkin_note_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onSaveCheckIn(selectedEmotion!!, note.trim())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = def.color),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth().testTag("checkin_save_btn")
                    ) {
                        Text("Complete Check-in", color = BgDark, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
