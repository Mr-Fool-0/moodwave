package com.example.service

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.math.*
import kotlin.random.Random

object SoundscapeEngine {
    private const val SAMPLE_RATE = 22050
    private var audioTrack: AudioTrack? = null
    private var audioJob: Job? = null
    private var isPlaying = false

    private var currentEmotion = "calm"
    private var volume = 0.35f
    private var ambienceMode = "none" // none, rain, ocean, wind

    private val chordMajor = listOf(
        listOf(261.63, 329.63, 392.00, 493.88), // C major 7
        listOf(220.00, 261.63, 329.63, 392.00), // A minor 7
        listOf(174.61, 220.00, 261.63, 329.63), // F major 7
        listOf(196.00, 246.94, 293.66, 329.63)  // G6
    )

    private val chordMinor = listOf(
        listOf(220.00, 261.63, 329.63, 392.00), // Am7
        listOf(174.61, 220.00, 261.63, 329.63), // Fmaj7
        listOf(261.63, 329.63, 392.00, 493.88), // Cmaj7
        listOf(164.81, 196.00, 246.94, 293.66)  // Em7
    )

    fun setEmotion(emotion: String) {
        currentEmotion = emotion.lowercase()
    }

    fun setVolume(vol: Float) {
        volume = vol.coerceIn(0f, 1f)
    }

    fun setAmbience(type: String) {
        ambienceMode = type.lowercase()
    }

    fun isRunning(): Boolean = isPlaying

    @Synchronized
    fun start(scope: CoroutineScope) {
        if (isPlaying) return
        isPlaying = true

        val minBuf = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufSize = max(minBuf, 4096)

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
        } catch (e: Exception) {
            isPlaying = false
            return
        }

        audioJob = scope.launch(Dispatchers.Default) {
            val buffer = ShortArray(bufSize / 2)
            var sampleIdx = 0L

            var lastNoteTime = 0L
            var noteFreq = 261.63
            var noteEnvelope = 0.0
            var fluteFreq = 523.25
            var fluteEnvelope = 0.0

            var oceanLfo = 0.0
            var windFilter = 0.0
            var rainFilter = 0.0

            while (isActive && isPlaying) {
                val isMinor = currentEmotion in listOf("sad", "anxious", "angry", "nostalgic")
                val chords = if (isMinor) chordMinor else chordMajor

                for (i in buffer.indices) {
                    val timeSec = sampleIdx.toDouble() / SAMPLE_RATE

                    // Chord trigger every 0.8s
                    val noteIntervalSamples = (SAMPLE_RATE * 0.8).toLong()
                    if (sampleIdx - lastNoteTime > noteIntervalSamples) {
                        lastNoteTime = sampleIdx
                        val chordIdx = ((sampleIdx / (noteIntervalSamples * 4)) % 4).toInt()
                        val step = ((sampleIdx / noteIntervalSamples) % 4).toInt()
                        noteFreq = chords[chordIdx][step]
                        noteEnvelope = 1.0

                        if (Random.nextFloat() < 0.65f) {
                            fluteFreq = chords[chordIdx][Random.nextInt(4)] * 2.0
                            fluteEnvelope = 0.8
                        }
                    }

                    // Piano harmonic voice
                    noteEnvelope = max(0.0, noteEnvelope - (1.0 / (SAMPLE_RATE * 1.8)))
                    val pianoTone = (
                        sin(2.0 * PI * noteFreq * timeSec) * 0.7 +
                        sin(4.0 * PI * noteFreq * timeSec) * 0.25 +
                        sin(6.0 * PI * noteFreq * timeSec) * 0.1
                    ) * noteEnvelope * 0.28

                    // Flute ethereal voice
                    fluteEnvelope = max(0.0, fluteEnvelope - (1.0 / (SAMPLE_RATE * 2.2)))
                    val fluteTone = sin(2.0 * PI * fluteFreq * timeSec) * fluteEnvelope * 0.15

                    // Ambience procedural generators
                    var ambienceSample = 0.0
                    val whiteNoise = Random.nextDouble(-1.0, 1.0)

                    when (ambienceMode) {
                        "rain" -> {
                            // Bandpass noise + occasional drop
                            rainFilter = rainFilter * 0.85 + whiteNoise * 0.15
                            val drop = if (Random.nextInt(400) == 0) sin(2.0 * PI * 1800.0 * timeSec) * 0.3 else 0.0
                            ambienceSample = (rainFilter * 0.4 + drop) * 0.45
                        }
                        "ocean" -> {
                            // Slow wave envelope modulator
                            oceanLfo = (sin(2.0 * PI * 0.12 * timeSec) + 1.0) * 0.5
                            rainFilter = rainFilter * 0.94 + whiteNoise * 0.06
                            ambienceSample = rainFilter * (oceanLfo * 0.7 + 0.15) * 0.8
                        }
                        "wind" -> {
                            // Low-frequency resonant breeze
                            windFilter = windFilter * 0.96 + whiteNoise * 0.04
                            val breezeLfo = (sin(2.0 * PI * 0.08 * timeSec) + 1.0) * 0.5
                            ambienceSample = windFilter * (0.3 + 0.7 * breezeLfo) * 0.9
                        }
                    }

                    val mixed = (pianoTone + fluteTone + ambienceSample) * volume
                    val clamped = mixed.coerceIn(-0.95, 0.95)
                    buffer[i] = (clamped * 32767).toInt().toShort()
                    sampleIdx++
                }

                audioTrack?.write(buffer, 0, buffer.size)
            }
        }
    }

    @Synchronized
    fun stop() {
        isPlaying = false
        audioJob?.cancel()
        audioJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
