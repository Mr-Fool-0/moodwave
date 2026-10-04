package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_checkins")
data class DailyCheckIn(
    @PrimaryKey
    val id: String = "chk_${System.currentTimeMillis()}",
    val date: String, // YYYY-MM-DD
    val emotion: String,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_items")
data class HabitItem(
    @PrimaryKey
    val id: String = "habit_${System.currentTimeMillis()}_${(100..999).random()}",
    val date: String, // YYYY-MM-DD
    val habitName: String,
    val isCompleted: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "time_capsules")
data class TimeCapsule(
    @PrimaryKey
    val id: String = "capsule_${System.currentTimeMillis()}",
    val content: String,
    val unlockDateMillis: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "emotion_corrections")
data class EmotionCorrection(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entryId: String,
    val originalEmotion: String,
    val correctedEmotion: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "journal_streaks")
data class JournalStreak(
    @PrimaryKey
    val id: String = "default_streak",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastJournalDate: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
