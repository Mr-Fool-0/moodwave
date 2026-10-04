package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.JournalEntry
import com.example.data.model.JournalStreak
import com.example.data.repository.MoodWaveRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MoodWaveViewModel(private val repository: MoodWaveRepository) : ViewModel() {

    val entries: StateFlow<List<JournalEntry>> = repository.allEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journalStreak: StateFlow<JournalStreak?> = repository.journalStreak
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current streak count computed reactively based on entries and persisted streak entity
    val currentStreakCount: StateFlow<Int> = combine(entries, journalStreak) { entryList, streakEntity ->
        computeCurrentStreak(entryList, streakEntity)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /**
     * View model function to track and update Journaling Streaks in Room database.
     */
    fun trackJournalingStreak(entryTimestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            updateJournalStreakSync(entryTimestamp)
        }
    }

    suspend fun updateJournalStreakSync(entryTimestamp: Long = System.currentTimeMillis()): JournalStreak {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(Date(entryTimestamp))

        val existingStreak = repository.getJournalStreak()
        val lastDate = existingStreak?.lastJournalDate ?: ""
        val currentCount = existingStreak?.currentStreak ?: 0
        val longestCount = existingStreak?.longestStreak ?: 0

        val cal = Calendar.getInstance().apply {
            time = Date(entryTimestamp)
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val yesterdayStr = sdf.format(cal.time)

        val newStreak = when {
            lastDate == todayStr -> if (currentCount == 0) 1 else currentCount
            lastDate == yesterdayStr -> currentCount + 1
            else -> 1
        }

        val newLongest = maxOf(longestCount, newStreak)

        val updatedEntity = JournalStreak(
            id = "default_streak",
            currentStreak = newStreak,
            longestStreak = newLongest,
            lastJournalDate = todayStr,
            updatedAt = System.currentTimeMillis()
        )

        repository.saveJournalStreak(updatedEntity)
        return updatedEntity
    }

    private fun computeCurrentStreak(entryList: List<JournalEntry>, storedStreak: JournalStreak?): Int {
        if (entryList.isEmpty()) {
            return storedStreak?.currentStreak ?: 0
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(Date())

        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val yesterdayStr = sdf.format(cal.time)

        val uniqueDates = entryList.map { sdf.format(Date(it.createdAt)) }.toSet()

        if (!uniqueDates.contains(todayStr) && !uniqueDates.contains(yesterdayStr)) {
            return 0
        }

        var streak = 0
        val checkCal = Calendar.getInstance()
        if (!uniqueDates.contains(todayStr)) {
            checkCal.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (true) {
            val checkDate = sdf.format(checkCal.time)
            if (uniqueDates.contains(checkDate)) {
                streak++
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        val storedCount = if (storedStreak?.lastJournalDate == todayStr || storedStreak?.lastJournalDate == yesterdayStr) {
            storedStreak.currentStreak
        } else 0

        return maxOf(streak, storedCount)
    }
}

class MoodWaveViewModelFactory(private val repository: MoodWaveRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoodWaveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoodWaveViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
