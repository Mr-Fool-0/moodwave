package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

data class TalkMessage(
    val id: String = UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String
)

@Composable
fun WaveTalkScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            TalkMessage(isUser = false, text = "How are your thoughts moving today? Speak freely—this space holds whatever frequency you bring.")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "WaveTalk",
            fontFamily = FontFamily.Serif,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "An introspective sounding board for unhurried thoughts.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chat stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    if (msg.isUser) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.82f)
                                .clip(RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp))
                                .background(Color(0x1AFFFFFF))
                                .border(1.dp, CardBorder, RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = msg.text,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = TextPrimary
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.88f)
                                .border(
                                    width = 2.dp,
                                    color = AccentDefault.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(0.dp)
                                )
                                .padding(start = 14.dp, top = 4.dp, bottom = 4.dp)
                        ) {
                            Text(
                                text = msg.text,
                                fontFamily = FontFamily.Serif,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = Color(0xFFD4D4DC)
                            )
                        }
                    }
                }
            }
        }

        // Input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text("Speak your mind...", color = TextMuted, fontSize = 14.sp)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = AccentDefault
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                    .testTag("wavetalk_input")
            )

            IconButton(
                onClick = {
                    val prompt = inputText.trim()
                    if (prompt.isNotBlank()) {
                        messages.add(TalkMessage(isUser = true, text = prompt))
                        inputText = ""
                        coroutineScope.launch {
                            listState.animateScrollToItem(messages.size - 1)
                            delay(600)
                            val analysis = EmotionEngine.analyze(prompt)
                            val def = EmotionEngine.getDef(analysis.dominantEmotion)
                            val response = when (analysis.dominantEmotion) {
                                "anxious" -> "Your words move fast here. If we strip away the urgency for just one breath, what is the single next thing actually in your control?"
                                "sad" -> "It makes sense that this feels heavy. You don't have to push it away or fix it in this exact minute. Allowing it to be true is enough."
                                "angry" -> "There is real energy behind that thought. Anger often guards something you hold dear. What is it protecting right now?"
                                "happy" -> "There is a lightness in your cadence. Hold onto this warmth—notice where in your body you feel it."
                                "nostalgic" -> "Looking backward can carry both warmth and a quiet ache. What part of that memory do you want to bring into today?"
                                else -> "Acknowledging uncertain thoughts is itself an act of grounding. How does it feel to see the thought written outside of your head?"
                            }
                            messages.add(TalkMessage(isUser = false, text = response))
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .background(AccentDefault)
                    .testTag("wavetalk_send_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = BgDark, modifier = Modifier.size(18.dp))
            }
        }
    }
}
