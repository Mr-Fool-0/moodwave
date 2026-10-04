package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        JournalEntry::class,
        DailyCheckIn::class,
        HabitItem::class,
        TimeCapsule::class,
        EmotionCorrection::class,
        JournalStreak::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MoodWaveDatabase : RoomDatabase() {
    abstract fun moodWaveDao(): MoodWaveDao

    companion object {
        @Volatile
        private var INSTANCE: MoodWaveDatabase? = null

        fun getInstance(context: Context): MoodWaveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoodWaveDatabase::class.java,
                    "moodwave_room.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
