package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("moodwave_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PROCEDURAL_AUDIO = "proc_audio_enabled"
        private const val KEY_AUDIO_VOLUME = "audio_volume"
        private const val KEY_AMBIENCE = "ambience_type" // none, rain, ocean, wind
        private const val KEY_DAILY_REMINDER = "daily_reminder_enabled"
        private const val KEY_REMINDER_TIME = "reminder_time"
        private const val KEY_REFLECTION_STYLE = "reflection_style"
    }

    private fun sha256(input: String): String {
        val bytes = (input + ":moodwave_salt").toByteArray()
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun isPinSet(): Boolean {
        return prefs.getString(KEY_PIN_HASH, null) != null
    }

    fun setPin(pin: String?) {
        if (pin.isNullOrEmpty()) {
            prefs.edit().remove(KEY_PIN_HASH).apply()
        } else {
            prefs.edit().putString(KEY_PIN_HASH, sha256(pin)).apply()
        }
    }

    fun verifyPin(pin: String): Boolean {
        val stored = prefs.getString(KEY_PIN_HASH, null) ?: return true
        return sha256(pin) == stored
    }

    var isProceduralAudioEnabled: Boolean
        get() = prefs.getBoolean(KEY_PROCEDURAL_AUDIO, false)
        set(value) = prefs.edit().putBoolean(KEY_PROCEDURAL_AUDIO, value).apply()

    var audioVolume: Float
        get() = prefs.getFloat(KEY_AUDIO_VOLUME, 0.4f)
        set(value) = prefs.edit().putFloat(KEY_AUDIO_VOLUME, value).apply()

    var ambienceType: String
        get() = prefs.getString(KEY_AMBIENCE, "none") ?: "none"
        set(value) = prefs.edit().putString(KEY_AMBIENCE, value).apply()

    var isDailyReminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_DAILY_REMINDER, false)
        set(value) = prefs.edit().putBoolean(KEY_DAILY_REMINDER, value).apply()

    var reminderTime: String
        get() = prefs.getString(KEY_REMINDER_TIME, "21:00") ?: "21:00"
        set(value) = prefs.edit().putString(KEY_REMINDER_TIME, value).apply()

    var reflectionStyle: String
        get() = prefs.getString(KEY_REFLECTION_STYLE, "gentle") ?: "gentle"
        set(value) = prefs.edit().putString(KEY_REFLECTION_STYLE, value).apply()
}
