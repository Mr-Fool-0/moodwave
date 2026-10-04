package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.EmotionEngine
import com.example.service.PosterGenerator
import com.example.ui.theme.*

@Composable
fun WaveArtScreen(
    currentEmotionKey: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTitle by remember { mutableStateOf("TODAY IN WAVES") }
    var selectedEmotion by remember { mutableStateOf(currentEmotionKey) }
    var posterBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(selectedTitle, selectedEmotion) {
        val def = EmotionEngine.getDef(selectedEmotion)
        posterBitmap = PosterGenerator.generatePosterBitmap(
            title = selectedTitle,
            emotionKey = selectedEmotion,
            intensity = 68,
            quote = def.poeticQuote
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Wave Art Poster",
            fontFamily = FontFamily.Serif,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Turn your emotional frequency into high-resolution artwork.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Poster Preview Card
        posterBitmap?.let { bmp ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                modifier = Modifier
                    .width(240.dp)
                    .aspectRatio(9f / 16f)
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .testTag("poster_preview_card")
            ) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Wave poster preview",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title Selection Chips
        val titles = listOf("TODAY IN WAVES", "WEEK IN MOTION", "QUIET DRIFT")
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            titles.forEach { t ->
                val isSel = selectedTitle == t
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSel) AccentDefault else SurfaceDark)
                        .border(1.dp, if (isSel) AccentDefault else CardBorder, RoundedCornerShape(12.dp))
                        .clickable { selectedTitle = t }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = t,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSel) BgDark else TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Export & Share Button
        Button(
            onClick = {
                posterBitmap?.let { bmp ->
                    val file = PosterGenerator.savePosterToFile(context, bmp)
                    PosterGenerator.sharePoster(context, file)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentDefault),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("export_poster_btn")
        ) {
            Icon(Icons.Default.Share, contentDescription = null, tint = BgDark, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export & Share Poster", color = BgDark, fontWeight = FontWeight.Medium, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
