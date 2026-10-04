package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodWaveDao {
    // Journal Entries
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: String): JournalEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntry)

    @Delete
    suspend fun deleteEntry(entry: JournalEntry)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteEntryById(id: String)

    @Query("UPDATE journal_entries SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: String, isFav: Boolean)

    @Query("UPDATE journal_entries SET dominantEmotion = :newEmotion WHERE id = :id")
    suspend fun updateEntryEmotion(id: String, newEmotion: String)

    // Daily Check-ins
    @Query("SELECT * FROM daily_checkins ORDER BY createdAt DESC")
    fun getAllCheckIns(): Flow<List<DailyCheckIn>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: DailyCheckIn)

    // Habits
    @Query("SELECT * FROM habit_items WHERE date = :date")
    fun getHabitsForDate(date: String): Flow<List<HabitItem>>

    @Query("SELECT * FROM habit_items ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<HabitItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitItem)

    @Query("DELETE FROM habit_items WHERE date = :date AND habitName = :name")
    suspend fun removeHabit(date: String, name: String)

    // Time Capsules
    @Query("SELECT * FROM time_capsules ORDER BY unlockDateMillis ASC")
    fun getAllCapsules(): Flow<List<TimeCapsule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapsule(capsule: TimeCapsule)

    @Delete
    suspend fun deleteCapsule(capsule: TimeCapsule)

    // Emotion Corrections
    @Query("SELECT * FROM emotion_corrections ORDER BY timestamp DESC")
    fun getAllCorrections(): Flow<List<EmotionCorrection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCorrection(correction: EmotionCorrection)

    // Journal Streaks
    @Query("SELECT * FROM journal_streaks WHERE id = 'default_streak' LIMIT 1")
    fun getJournalStreakFlow(): Flow<JournalStreak?>

    @Query("SELECT * FROM journal_streaks WHERE id = 'default_streak' LIMIT 1")
    suspend fun getJournalStreak(): JournalStreak?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streak: JournalStreak)
}
