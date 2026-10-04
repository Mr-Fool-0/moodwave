package com.example.service

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class AudioRecorderManager(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentOutputFile: File? = null

    var isRecording = false
        private set

    var isPlaying = false
        private set

    fun startRecording(): File? {
        if (isRecording) return currentOutputFile
        return try {
            val audioDir = File(context.filesDir, "voice_notes")
            if (!audioDir.exists()) audioDir.mkdirs()

            val file = File(audioDir, "note_${System.currentTimeMillis()}.m4a")
            currentOutputFile = file

            mediaRecorder = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            file
        } catch (e: Exception) {
            e.printStackTrace()
            stopRecording()
            null
        }
    }

    fun stopRecording(): String? {
        if (!isRecording) return null
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            currentOutputFile?.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            isRecording = false
            mediaRecorder = null
            null
        }
    }

    fun playAudio(path: String, onFinished: () -> Unit = {}) {
        stopAudio()
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(path)
                prepare()
                setOnCompletionListener {
                    this@AudioRecorderManager.isPlaying = false
                    onFinished()
                }
                start()
            }
            this@AudioRecorderManager.isPlaying = true
        } catch (e: Exception) {
            e.printStackTrace()
            this@AudioRecorderManager.isPlaying = false
        }
    }

    fun stopAudio() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
            mediaPlayer = null
            isPlaying = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
