package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitItem
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HabitsScreen(
    todayHabits: List<HabitItem>,
    onToggleHabit: (habitName: String, isDone: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultHabits = remember {
        listOf(
            "Sleep 7+ hrs",
            "Physical Walk / Movement",
            "Quiet Reading",
            "Breathing / Meditation",
            "Hydration (2 Liters)",
            "Sunlight & Fresh Air"
        )
    }

    val todayDateStr = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Habits & Grounding",
            fontFamily = FontFamily.Serif,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Notice how everyday rhythms shape the wave of your mind.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "TODAY'S GROUNDING PRACTICES",
            fontFamily = FontFamily.SansSerif,
            fontSize = 11.sp,
            letterSpacing = 1.4.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                defaultHabits.forEachIndexed { index, habitName ->
                    val isDone = todayHabits.any { it.habitName == habitName && it.isCompleted }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleHabit(habitName, !isDone) }
                            .padding(vertical = 12.dp)
                            .testTag("habit_row_$index"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = habitName,
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp,
                            color = if (isDone) TextPrimary else Color(0xFFC6C6CE)
                        )

                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDone) AccentDefault else Color(0x11FFFFFF))
                                .border(1.dp, if (isDone) AccentDefault else CardBorder, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = BgDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    if (index < defaultHabits.size - 1) {
                        HorizontalDivider(color = CardBorder, thickness = 0.6.dp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Observational Correlations
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "OBSERVATIONAL CORRELATION",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your entries suggest that days following sustained rest and unhurried physical movement register a significantly higher calm-to-anxious ratio.",
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFFD4D4DC)
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
