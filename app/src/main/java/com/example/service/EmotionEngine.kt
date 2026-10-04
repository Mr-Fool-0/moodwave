package com.example.service

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class EmotionDef(
    val key: String,
    val name: String,
    val color: Color,
    val hexString: String,
    val speed: Float,
    val amplitude: Float,
    val turbulence: Float,
    val valence: Int, // 1 = positive, -1 = negative, 0 = neutral/reflective
    val description: String,
    val poeticQuote: String,
    val toneLine: String
)

data class ProgressionStep(
    val position: Float,
    val emotion: String,
    val intensity: Int
)

data class EmotionAnalysis(
    val dominantEmotion: String,
    val secondaryEmotions: List<String>,
    val intensity: Int,
    val confidence: Int,
    val sentiment: String,
    val themes: List<String>,
    val reflection: String,
    val progression: List<ProgressionStep>,
    val cues: List<String>,
    val quote: String
)

object EmotionEngine {
    val EMOTIONS = mapOf(
        "calm" to EmotionDef(
            key = "calm",
            name = "Calm",
            color = ColorCalm,
            hexString = "#C1C1C9",
            speed = 0.5f,
            amplitude = 0.55f,
            turbulence = 0f,
            valence = 1,
            description = "Slow, even breathing. Nothing is asking for you.",
            poeticQuote = "Some days are meant to arrive softly, asking for nothing in return.",
            toneLine = "Your thoughts feel quieter today."
        ),
        "happy" to EmotionDef(
            key = "happy",
            name = "Happy",
            color = ColorHappy,
            hexString = "#F0F0F4",
            speed = 1.2f,
            amplitude = 0.9f,
            turbulence = 0.1f,
            valence = 1,
            description = "Light moving quickly, warm at the edges.",
            poeticQuote = "Light moves quickly when the heart forgets to hold its breath.",
            toneLine = "There is a brightness in your recent words."
        ),
        "sad" to EmotionDef(
            key = "sad",
            name = "Sad",
            color = ColorSad,
            hexString = "#737380",
            speed = 0.35f,
            amplitude = 0.45f,
            turbulence = 0f,
            valence = -1,
            description = "Heavy air, slow water. Nothing needs fixing right now.",
            poeticQuote = "Even heavy water eventually finds its quiet way forward.",
            toneLine = "Your recent writing feels heavy, and that is allowed."
        ),
        "anxious" to EmotionDef(
            key = "anxious",
            name = "Anxious",
            color = ColorAnxious,
            hexString = "#83838E",
            speed = 1.3f,
            amplitude = 0.8f,
            turbulence = 1f,
            valence = -1,
            description = "An uneven signal: quick, restless, hard to settle.",
            poeticQuote = "The storm inside is loud, but your feet are firmly on the earth.",
            toneLine = "Your thoughts seem to be moving fast lately."
        ),
        "angry" to EmotionDef(
            key = "angry",
            name = "Angry",
            color = ColorAngry,
            hexString = "#FFFFFF",
            speed = 1.6f,
            amplitude = 1.0f,
            turbulence = 0.3f,
            valence = -1,
            description = "Sharp edges and stored energy looking for somewhere to go.",
            poeticQuote = "A fire burns cleanest when given space rather than fuel.",
            toneLine = "There is some fire in your recent entries."
        ),
        "nostalgic" to EmotionDef(
            key = "nostalgic",
            name = "Nostalgic",
            color = ColorNostalgic,
            hexString = "#A2A2AC",
            speed = 0.4f,
            amplitude = 0.6f,
            turbulence = 0f,
            valence = 0,
            description = "Warm memories with a trace of distance.",
            poeticQuote = "We look back not to return, but to thank what shaped our hands.",
            toneLine = "Your mind keeps drifting somewhere warm and far."
        ),
        "hopeful" to EmotionDef(
            key = "hopeful",
            name = "Hopeful",
            color = ColorHopeful,
            hexString = "#E0E0E5",
            speed = 0.7f,
            amplitude = 0.7f,
            turbulence = 0f,
            valence = 1,
            description = "A slow brightening, something rising at the edge of the frame.",
            poeticQuote = "Tomorrow doesn’t need certainty to begin; it only needs daybreak.",
            toneLine = "Something in your writing is turning toward the light."
        ),
        "loving" to EmotionDef(
            key = "loving",
            name = "Loving",
            color = ColorLoving,
            hexString = "#D1D1D8",
            speed = 0.6f,
            amplitude = 0.65f,
            turbulence = 0f,
            valence = 1,
            description = "Close, warm, unhurried.",
            poeticQuote = "To keep someone warm in your thoughts is a quiet grace.",
            toneLine = "Your recent words are full of the people who matter."
        ),
        "reflective" to EmotionDef(
            key = "reflective",
            name = "Reflective",
            color = ColorReflective,
            hexString = "#B2B2BB",
            speed = 0.5f,
            amplitude = 0.6f,
            turbulence = 0.05f,
            valence = 0,
            description = "Quiet attention turned inward.",
            poeticQuote = "The mirror does not judge the water; it merely lets it settle.",
            toneLine = "Your thoughts feel introspective today."
        ),
        "neutral" to EmotionDef(
            key = "neutral",
            name = "Neutral",
            color = ColorNeutral,
            hexString = "#93939D",
            speed = 0.45f,
            amplitude = 0.4f,
            turbulence = 0f,
            valence = 0,
            description = "Even and unhurried, a pause between states.",
            poeticQuote = "A still surface holds room for whatever the sky brings next.",
            toneLine = "Things feel even right now."
        )
    )

    fun getDef(key: String): EmotionDef = EMOTIONS[key.lowercase()] ?: EMOTIONS["calm"]!!

    private val LEXICON_RAW = mapOf(
        "happy" to "happy:2 happiness:2 joy:3 glad:2 delight:3 cheer:2 laugh:2 smile:2 smiling:2 fun:2 enjoy:2 excited:2 exciting:2 thrill:3 celebrate:2 wonderful:3 amazing:2 awesome:2 great:1 good:1 proud:3 grateful:2 blessed:2 lucky:2 success:2 fantastic:3 yay:2 haha:1 won:2 victory:3 promoted:3 khush:3 maza:2 shandar:3 mast:2 zabardast:3 badiya:2 cheerful:2 bliss:3 content:2",
        "sad" to "sad:3 sadness:3 cry:3 crying:3 tears:2 tearful:3 lonely:3 alone:2 miss:2 lost:2 empty:3 hurt:2 heartbroken:4 heartbreak:4 grief:4 grieve:3 gloomy:3 depressed:4 depressing:4 hopeless:4 numb:3 tired:1 exhausted:2 drained:2 pretend:2 unhappy:3 disappoint:3 regret:2 sorrow:3 mourn:3 broken:2 worthless:4 failure:3 failed:2 rejected:3 ignored:2 abandoned:3 misunderstood:3 unwanted:3 udaas:3 rona:2 akela:2 dukh:3 low:1 down:1 dard:3 gam:3 maayoos:3",
        "anxious" to "anxious:3 anxiety:3 worry:3 worried:3 worrying:3 nervous:3 scared:3 afraid:3 fear:3 panic:4 stress:3 stressed:3 overthinking:3 overthink:3 uneasy:3 restless:2 tense:2 dread:3 uncertain:2 unsure:2 doubt:2 pressure:2 deadline:2 exam:2 overwhelm:3 overwhelmed:3 insecure:3 shaky:2 jitter:2 racing:2 chinta:3 darr:3 ghabra:3 tension:2 sleepless:2 insomnia:2 interview:1 pareshan:3 fikar:2 khauf:3 palpitations:3",
        "angry" to "angry:3 anger:3 mad:2 furious:4 rage:4 hate:3 annoy:2 annoyed:2 annoying:2 irritate:3 irritated:3 frustrating:3 frustrated:3 unfair:2 pissed:3 resent:3 jealous:2 betray:3 betrayed:3 disrespect:3 yell:2 shout:2 argue:2 fighting:2 fight:2 fought:2 rude:2 selfish:2 blame:2 gussa:3 fuming:4 livid:4 bitter:2 nafrat:3 bhadak:3",
        "calm" to "calm:3 peace:3 peaceful:3 quiet:2 relax:3 relaxed:3 relaxing:2 gentle:2 rest:2 resting:2 breathe:2 breathing:2 serene:3 tranquil:3 steady:2 soothing:3 cozy:2 comfort:2 balanced:2 slow:1 still:1 fine:1 okay:1 shaanti:3 sukoon:3 stillness:3 silence:2 mellow:2 settled:2 grounded:3 breeze:2 chain:3 aaram:2",
        "nostalgic" to "remember:2 memories:2 memory:2 nostalgic:4 nostalgia:4 childhood:3 past:2 reminisce:4 photo:1 photograph:1 yesterday:1 old:1 yaad:3 yaadein:3 purana:2 flashback:3 throwback:3 school:1 reunion:2 hometown:2 vintage:1 purani:3 bachpan:3 beete:2 zamana:2 archive:1 longing:2",
        "hopeful" to "hope:3 hopeful:3 optimistic:3 future:1 someday:2 tomorrow:1 better:1 begin:1 beginning:1 promise:2 possible:1 possibility:2 chance:1 dream:2 goal:2 aspire:2 faith:2 trust:1 healing:3 heal:2 improve:2 progress:2 umeed:3 ummeed:3 brighter:3 sunrise:2 opportunity:2 courage:2 brave:2 recover:2",
        "loving" to "love:3 loved:3 loving:3 lovely:2 care:2 caring:3 affectionate:3 affection:3 adore:3 cherish:3 hug:2 kiss:2 dear:1 sweet:1 together:1 family:1 friend:1 mom:1 dad:1 mother:1 father:1 sister:1 brother:1 partner:1 grateful:2 thankful:2 thanks:1 appreciate:2 pyaar:3 pyar:3 ishq:3 maa:1 papa:1 bestie:2 crush:2 cuddle:2 dost:2 mohabbat:3",
        "reflective" to "realize:3 realized:3 reflect:3 reflecting:3 think:1 thought:1 wonder:2 understand:2 learn:2 lesson:2 pattern:2 notice:2 insight:3 meaning:2 purpose:2 introspect:3 honest:1 myself:1 growth:2 question:1 ponder:3 aware:2 awareness:2 acceptance:2 accept:2 perspective:2 mindset:2 sochna:2 samajh:2 vichar:2 dhyan:2 contemplate:3"
    )

    private val WORD_MAP = mutableMapOf<String, Pair<String, Float>>()

    init {
        LEXICON_RAW.forEach { (emotion, text) ->
            text.split(" ").forEach { item ->
                val parts = item.split(":")
                if (parts.size == 2) {
                    val word = parts[0].trim().lowercase()
                    val weight = parts[1].toFloatOrNull() ?: 1f
                    WORD_MAP[word] = Pair(emotion, weight)
                }
            }
        }
    }

    private val NEGATIONS = setOf("not", "no", "never", "dont", "didn't", "didnt", "cant", "can't", "wont", "won't", "isnt", "isn't", "wasnt", "wasn't", "without", "neither", "nor", "nahi", "mat", "hardly", "barely")
    private val INTENSIFIERS = mapOf("very" to 1.4f, "really" to 1.4f, "so" to 1.3f, "extremely" to 1.6f, "deeply" to 1.5f, "totally" to 1.3f, "bahut" to 1.4f, "zyada" to 1.3f)

    private val PHRASES = listOf(
        Triple("tired of", "sad", 2f),
        Triple("fed up", "angry", 3f),
        Triple("cant stop thinking", "anxious", 3f),
        Triple("can't stop thinking", "anxious", 3f),
        Triple("on edge", "anxious", 3f),
        Triple("what if", "anxious", 2f),
        Triple("at peace", "calm", 4f),
        Triple("so proud", "happy", 3f),
        Triple("feel alone", "sad", 3f),
        Triple("give up", "sad", 2f),
        Triple("used to", "nostalgic", 2f),
        Triple("back then", "nostalgic", 3f),
        Triple("in love", "loving", 4f),
        Triple("not okay", "sad", 2f),
        Triple("let it be", "calm", 3f),
        Triple("dil dukha", "sad", 3f),
        Triple("sukoon mila", "calm", 3f)
    )

    private val THEME_PATTERNS = mapOf(
        "Memory" to listOf("remember", "memory", "memories", "childhood", "nostalgic", "past", "yaad", "bachpan"),
        "Relationships" to listOf("friend", "family", "mom", "dad", "mother", "father", "sister", "brother", "partner", "love", "together", "dost", "pyaar"),
        "Future" to listOf("future", "tomorrow", "someday", "plan", "career", "goal", "dream", "koshish", "umeed"),
        "College & Work" to listOf("college", "exam", "class", "project", "work", "job", "boss", "office", "interview", "kaam", "salary"),
        "Self-growth" to listOf("realize", "learn", "myself", "growth", "discipline", "understand", "samajh"),
        "Loneliness" to listOf("alone", "lonely", "isolated", "nobody", "miss", "akela"),
        "Rest & Energy" to listOf("sleep", "tired", "energy", "exhausted", "rest", "neend", "aaram")
    )

    fun analyze(text: String): EmotionAnalysis {
        val lower = text.lowercase().replace("’", "'").replace("‘", "'")
        val scores = mutableMapOf<String, Float>()
        val detectedCues = mutableMapOf<String, MutableList<String>>()

        fun addScore(emotion: String, weight: Float, cue: String) {
            scores[emotion] = (scores[emotion] ?: 0f) + weight
            val list = detectedCues.getOrPut(emotion) { mutableListOf() }
            if (list.size < 5 && !list.contains(cue)) {
                list.add(cue)
            }
        }

        // Phrase scoring
        PHRASES.forEach { (phrase, emotion, weight) ->
            if (lower.contains(phrase)) {
                addScore(emotion, weight, phrase)
            }
        }

        // Token scoring
        val sentences = lower.split(Regex("[.!?\\n;]+"))
        var wordCount = 0

        sentences.forEach { sentence ->
            val tokens = Regex("[a-z']+").findAll(sentence).map { it.value }.toList()
            wordCount += tokens.size
            var intensifier = 1f
            var lastIntensifierIdx = -99

            tokens.forEachIndexed { i, token ->
                if (INTENSIFIERS.containsKey(token)) {
                    intensifier = INTENSIFIERS[token] ?: 1.3f
                    lastIntensifierIdx = i
                    return@forEachIndexed
                }

                val currentMultiplier = if (i - lastIntensifierIdx <= 2) intensifier else 1f
                val match = WORD_MAP[token] ?: run {
                    // Try simple stem checks
                    val stem = when {
                        token.endsWith("ing") && token.length > 5 -> token.dropLast(3)
                        token.endsWith("ed") && token.length > 4 -> token.dropLast(2)
                        token.endsWith("s") && token.length > 3 -> token.dropLast(1)
                        else -> null
                    }
                    if (stem != null) WORD_MAP[stem] else null
                }

                if (match != null) {
                    val (emotion, baseWeight) = match
                    val isNegated = (max(0, i - 3) until i).any { idx -> NEGATIONS.contains(tokens[idx]) }
                    if (isNegated) {
                        val opposite = when (emotion) {
                            "happy" -> "sad"
                            "calm" -> "anxious"
                            "hopeful" -> "sad"
                            "loving" -> "sad"
                            else -> "neutral"
                        }
                        if (opposite != "neutral") {
                            addScore(opposite, baseWeight * 0.7f * currentMultiplier, "not $token")
                        }
                    } else {
                        addScore(emotion, baseWeight * currentMultiplier, token)
                    }
                }
            }
        }

        val totalScore = scores.values.sum()
        val ranked = scores.entries.sortedByDescending { it.value }

        val dominant = if (totalScore >= 1.2f && ranked.isNotEmpty()) ranked[0].key else "neutral"
        val secondaries = if (dominant == "neutral") emptyList() else {
            val topScore = ranked.firstOrNull()?.value ?: 1f
            ranked.drop(1).filter { it.value >= topScore * 0.4f }.take(2).map { it.key }
        }

        // Intensity calculation (15..98)
        val densityFactor = if (wordCount > 0) min(1.5f, totalScore / sqrt(wordCount.toFloat())) else 0.5f
        val calculatedIntensity = (20 + densityFactor * 45).toInt().coerceIn(15, 98)

        // Confidence calculation (40..97)
        val share = if (totalScore > 0f && ranked.isNotEmpty()) ranked[0].value / totalScore else 0.4f
        val calculatedConfidence = (40 + (share * 45)).toInt().coerceIn(40, 97)

        // Sentiment calculation
        val posSum = ranked.filter { (EMOTIONS[it.key]?.valence ?: 0) > 0 }.sumOf { it.value.toDouble() }
        val negSum = ranked.filter { (EMOTIONS[it.key]?.valence ?: 0) < 0 }.sumOf { it.value.toDouble() }
        val sentiment = when {
            totalScore < 1.2f -> "neutral"
            posSum > 1.2 && negSum > 1.2 && (min(posSum, negSum) / max(posSum, negSum) >= 0.45) -> "mixed"
            posSum > negSum * 1.15 -> "positive"
            negSum > posSum * 1.15 -> "negative"
            else -> "neutral"
        }

        // Themes
        val foundThemes = THEME_PATTERNS.filter { (_, keywords) ->
            keywords.any { lower.contains(it) }
        }.keys.take(3).toList()
        val finalThemes = if (foundThemes.isNotEmpty()) foundThemes else listOf("Everyday life")

        // 4-segment Progression
        val segments = splitIntoFourSegments(text)
        var lastEmotion = dominant
        val progression = segments.mapIndexed { idx, segment ->
            val segAnalysis = analyzeSegment(segment)
            val em = if (segAnalysis.first != "neutral") segAnalysis.first else lastEmotion
            lastEmotion = em
            ProgressionStep(
                position = idx / 3.0f,
                emotion = em,
                intensity = if (segAnalysis.second > 0) segAnalysis.second else calculatedIntensity
            )
        }

        val def = getDef(dominant)
        val reflection = "${def.toneLine} ${def.description}"

        return EmotionAnalysis(
            dominantEmotion = dominant,
            secondaryEmotions = secondaries,
            intensity = calculatedIntensity,
            confidence = calculatedConfidence,
            sentiment = sentiment,
            themes = finalThemes,
            reflection = reflection,
            progression = progression,
            cues = detectedCues[dominant] ?: emptyList(),
            quote = def.poeticQuote
        )
    }

    private fun splitIntoFourSegments(text: String): List<String> {
        val words = text.trim().split(Regex("\\s+"))
        if (words.size < 4) return listOf(text, text, text, text)
        val chunkSize = (words.size + 3) / 4
        return (0..3).map { i ->
            val start = min(words.size, i * chunkSize)
            val end = min(words.size, (i + 1) * chunkSize)
            if (start < end) words.subList(start, end).joinToString(" ") else words.last()
        }
    }

    private fun analyzeSegment(segment: String): Pair<String, Int> {
        val lower = segment.lowercase()
        val scores = mutableMapOf<String, Float>()
        WORD_MAP.forEach { (word, pair) ->
            if (lower.contains(word)) {
                scores[pair.first] = (scores[pair.first] ?: 0f) + pair.second
            }
        }
        val top = scores.entries.maxByOrNull { it.value }
        return if (top != null && top.value >= 1f) {
            Pair(top.key, (top.value * 25).toInt().coerceIn(20, 95))
        } else {
            Pair("neutral", 35)
        }
    }
}
