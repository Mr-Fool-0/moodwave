package com.example.data.repository

import com.example.data.local.MoodWaveDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class MoodWaveRepository(private val dao: MoodWaveDao) {
    val allEntries: Flow<List<JournalEntry>> = dao.getAllEntries()
    val allCheckIns: Flow<List<DailyCheckIn>> = dao.getAllCheckIns()
    val allHabits: Flow<List<HabitItem>> = dao.getAllHabits()
    val allCapsules: Flow<List<TimeCapsule>> = dao.getAllCapsules()
    val allCorrections: Flow<List<EmotionCorrection>> = dao.getAllCorrections()

    suspend fun getEntryById(id: String): JournalEntry? = dao.getEntryById(id)

    suspend fun insertEntry(entry: JournalEntry) = dao.insertEntry(entry)

    suspend fun deleteEntry(entry: JournalEntry) = dao.deleteEntry(entry)

    suspend fun deleteEntryById(id: String) = dao.deleteEntryById(id)

    suspend fun setFavorite(id: String, isFav: Boolean) = dao.setFavorite(id, isFav)

    suspend fun updateEntryEmotion(id: String, emotion: String) = dao.updateEntryEmotion(id, emotion)

    suspend fun insertCheckIn(checkIn: DailyCheckIn) = dao.insertCheckIn(checkIn)

    fun getHabitsForDate(date: String): Flow<List<HabitItem>> = dao.getHabitsForDate(date)

    suspend fun insertHabit(habit: HabitItem) = dao.insertHabit(habit)

    suspend fun removeHabit(date: String, name: String) = dao.removeHabit(date, name)

    suspend fun insertCapsule(capsule: TimeCapsule) = dao.insertCapsule(capsule)

    suspend fun deleteCapsule(capsule: TimeCapsule) = dao.deleteCapsule(capsule)

    suspend fun insertCorrection(correction: EmotionCorrection) = dao.insertCorrection(correction)

    val journalStreak: Flow<JournalStreak?> = dao.getJournalStreakFlow()

    suspend fun getJournalStreak(): JournalStreak? = dao.getJournalStreak()

    suspend fun saveJournalStreak(streak: JournalStreak) = dao.insertOrUpdateStreak(streak)
}
