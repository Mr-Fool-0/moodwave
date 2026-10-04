package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey
    val id: String = "entry_${System.currentTimeMillis()}",
    val title: String = "",
    val content: String,
    val dominantEmotion: String = "calm",
    val secondaryEmotions: String = "", // Comma-separated
    val intensity: Int = 50,
    val confidence: Int = 70,
    val sentiment: String = "neutral", // positive, negative, neutral, mixed
    val themes: String = "Everyday life", // Comma-separated
    val cues: String = "", // Comma-separated
    val progressionJson: String = "", // JSON representation of 4-step wave
    val audioPath: String? = null,
    val photoUris: String = "", // Comma-separated
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
