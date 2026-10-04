package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AudioPlayerDock(
    isPlaying: Boolean,
    ambienceName: String,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xCC0D0E15))
            .border(1.dp, CardBorder, RoundedCornerShape(32.dp))
            .clickable { onTogglePlay() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("audio_player_dock")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.MusicNote else Icons.Default.MusicOff,
                contentDescription = "Soundscape",
                tint = if (isPlaying) AccentDefault else TextMuted,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = if (isPlaying) {
                    if (ambienceName != "none") "Soundscape · ${ambienceName.capitalize()}" else "Procedural Soundscape"
                } else "Soundscape Muted",
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                color = if (isPlaying) TextPrimary else TextSecondary
            )

            if (isPlaying) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = AccentDefault,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
