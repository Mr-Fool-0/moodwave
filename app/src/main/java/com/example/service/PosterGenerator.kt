package com.example.service

import android.content.Context
import android.content.Intent
import android.graphics.*
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import kotlin.math.sin

object PosterGenerator {

    fun generatePosterBitmap(
        title: String,
        emotionKey: String,
        intensity: Int,
        quote: String
    ): Bitmap {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val emotionDef = EmotionEngine.getDef(emotionKey)
        val colorInt = android.graphics.Color.parseColor(emotionDef.hexString)

        // Background dark gradient
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                intArrayOf(
                    android.graphics.Color.rgb(7, 8, 12),
                    android.graphics.Color.rgb(11, 12, 20),
                    android.graphics.Color.rgb(7, 8, 12)
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Atmospheric center glow
        val glowPaint = Paint().apply {
            shader = RadialGradient(
                540f, 960f, 650f,
                intArrayOf(
                    android.graphics.Color.argb(45, 178, 178, 187),
                    android.graphics.Color.TRANSPARENT
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(540f, 960f, 650f, glowPaint)

        // Wave Curves
        val wavePaint = Paint().apply {
            color = colorInt
            style = Paint.Style.STROKE
            strokeWidth = 5f
            isAntiAlias = true
        }

        val amp = 140f * (intensity / 100f)
        val path = Path()
        for (x in 0..width step 8) {
            val y = 960f +
                sin(x * 0.012f) * amp +
                sin(x * 0.024f) * (amp * 0.35f)
            if (x == 0) path.moveTo(x.toFloat(), y) else path.lineTo(x.toFloat(), y)
        }
        canvas.drawPath(path, wavePaint)

        // Secondary subtle wave
        val wavePaint2 = Paint().apply {
            color = android.graphics.Color.argb(90, Color.red(colorInt), Color.green(colorInt), Color.blue(colorInt))
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        val path2 = Path()
        for (x in 0..width step 8) {
            val y = 1040f + sin(x * 0.014f + 1.2f) * (amp * 0.75f)
            if (x == 0) path2.moveTo(x.toFloat(), y) else path2.lineTo(x.toFloat(), y)
        }
        canvas.drawPath(path2, wavePaint2)

        // Branding Label
        val labelPaint = Paint().apply {
            color = android.graphics.Color.rgb(139, 139, 149)
            textSize = 28f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            letterSpacing = 0.25f
        }
        canvas.drawText("MOODWAVE · YOUR EMOTIONS HAVE A FREQUENCY", 540f, 260f, labelPaint)

        // Title
        val titlePaint = Paint().apply {
            color = android.graphics.Color.rgb(236, 236, 240)
            textSize = 68f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(title, 540f, 380f, titlePaint)

        // Emotion & Intensity
        val emotionPaint = Paint().apply {
            color = colorInt
            textSize = 54f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("${emotionDef.name} · $intensity%", 540f, 840f, emotionPaint)

        // Quote
        val quotePaint = Paint().apply {
            color = android.graphics.Color.rgb(212, 212, 220)
            textSize = 34f
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Wrap quote text into lines
        val maxTextWidth = 860f
        val words = quote.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""
        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (quotePaint.measureText(testLine) < maxTextWidth) {
                currentLine = testLine
            } else {
                lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)

        var quoteY = 1380f
        for (line in lines) {
            canvas.drawText("“$line”", 540f, quoteY, quotePaint)
            quoteY += 52f
        }

        return bitmap
    }

    fun savePosterToFile(context: Context, bitmap: Bitmap): File {
        val dir = File(context.cacheDir, "posters")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "moodwave_poster_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    fun sharePoster(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share MoodWave Poster"))
    }
}
