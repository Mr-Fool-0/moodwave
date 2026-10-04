package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.MoodWaveDatabase
import com.example.data.model.JournalEntry
import com.example.data.model.JournalStreak
import com.example.data.repository.MoodWaveRepository
import com.example.service.EmotionEngine
import com.example.ui.MoodWaveViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MoodWave", appName)
    }

    @Test
    fun `test emotion engine calm detection`() {
        val analysis = EmotionEngine.analyze("I feel at peace and calm today after resting")
        assertEquals("calm", analysis.dominantEmotion)
        assertNotNull(analysis.quote)
    }

    @Test
    fun `test emotion engine multilingual hindi hinglish`() {
        val analysis = EmotionEngine.analyze("bahut sukoon mila aur shaanti lag rahi hai")
        assertEquals("calm", analysis.dominantEmotion)
    }

    @Test
    fun `test journaling streak database entity and tracking`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = MoodWaveDatabase.getInstance(context)
        val repository = MoodWaveRepository(database.moodWaveDao())
        val viewModel = MoodWaveViewModel(repository)

        // Track a streak synchronously
        val savedStreak = viewModel.updateJournalStreakSync(System.currentTimeMillis())
        assertEquals("default_streak", savedStreak.id)
        assert(savedStreak.currentStreak >= 1)

        val retrieved = repository.getJournalStreak()
        assertNotNull(retrieved)
        assertEquals(savedStreak.currentStreak, retrieved?.currentStreak)
    }
}
