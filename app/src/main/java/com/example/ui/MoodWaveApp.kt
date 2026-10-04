package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppPreferences
import com.example.data.local.MoodWaveDatabase
import com.example.data.model.*
import com.example.data.repository.MoodWaveRepository
import com.example.service.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class Screen(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    JOURNAL("Journal", Icons.Default.Create),
    CHECK_IN("Check-in", Icons.Default.HourglassEmpty),
    HABITS("Habits", Icons.Default.CheckCircleOutline),
    TIMELINE("Timeline", Icons.Default.Timeline),
    REVIEWS("Reviews", Icons.Default.AutoGraph),
    WEATHER("Weather", Icons.Default.WbCloudy),
    CAPSULES("Capsules", Icons.Default.LockClock),
    EXERCISES("Breathe", Icons.Default.SelfImprovement),
    WAVE_ART("Wave Art", Icons.Default.Palette),
    ANALYZER("Analyzer", Icons.Default.Tune),
    WAVE_TALK("WaveTalk", Icons.Default.Forum),
    SETTINGS("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodWaveApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val database = remember { MoodWaveDatabase.getInstance(context) }
    val repository = remember { MoodWaveRepository(database.moodWaveDao()) }
    val viewModel: MoodWaveViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = MoodWaveViewModelFactory(repository)
    )
    val prefs = remember { AppPreferences(context) }
    val audioRecorderManager = remember { AudioRecorderManager(context) }

    val entries by viewModel.entries.collectAsStateWithLifecycle()
    val streakCount by viewModel.currentStreakCount.collectAsStateWithLifecycle()
    val checkIns by repository.allCheckIns.collectAsStateWithLifecycle(initialValue = emptyList())
    val habits by repository.allHabits.collectAsStateWithLifecycle(initialValue = emptyList())
    val capsules by repository.allCapsules.collectAsStateWithLifecycle(initialValue = emptyList())

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val todayHabits = habits.filter { it.date == todayStr }

    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedEntryForDetail by remember { mutableStateOf<JournalEntry?>(null) }
    var isPlayingVoiceNote by remember { mutableStateOf(false) }

    // PIN lock state
    var isUnlocked by remember { mutableStateOf(!prefs.isPinSet()) }

    // Procedural soundscape state
    var isSoundscapePlaying by remember { mutableStateOf(prefs.isProceduralAudioEnabled) }
    var currentAmbience by remember { mutableStateOf(prefs.ambienceType) }

    // Dynamic emotion tracking
    val dominantEmotionKey = remember(entries) {
        entries.firstOrNull()?.dominantEmotion ?: "calm"
    }
    val currentEmotionDef = remember(dominantEmotionKey) {
        EmotionEngine.getDef(dominantEmotionKey)
    }

    LaunchedEffect(isSoundscapePlaying, currentEmotionDef, currentAmbience) {
        if (isSoundscapePlaying) {
            SoundscapeEngine.setEmotion(currentEmotionDef.key)
            SoundscapeEngine.setVolume(prefs.audioVolume)
            SoundscapeEngine.setAmbience(currentAmbience)
            SoundscapeEngine.start(scope)
        } else {
            SoundscapeEngine.stop()
        }
    }

    // Android Back Button handling
    BackHandler {
        if (selectedEntryForDetail != null) {
            selectedEntryForDetail = null
        } else if (currentScreen != Screen.HOME) {
            currentScreen = Screen.HOME
        } else {
            // Minimize or exit app cleanly
            (context as? android.app.Activity)?.finish()
        }
    }

    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    MoodWaveTheme(accentColor = currentEmotionDef.color) {
        if (!isUnlocked) {
            PinLockOverlay(
                onUnlocked = { isUnlocked = true },
                onVerifyPin = { pin -> prefs.verifyPin(pin) }
            )
        } else {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = SurfaceDark,
                        drawerContentColor = TextPrimary,
                        modifier = Modifier.width(280.dp)
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "MoodWave",
                                fontFamily = FontFamily.Serif,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Normal,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "FREQUENCY MAP",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            letterSpacing = 1.4.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )

                        HorizontalDivider(color = CardBorder, modifier = Modifier.padding(vertical = 8.dp))

                        Screen.values().forEach { screen ->
                            NavigationDrawerItem(
                                label = { Text(screen.title, fontFamily = FontFamily.SansSerif, fontSize = 14.sp) },
                                icon = { Icon(screen.icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                selected = currentScreen == screen,
                                onClick = {
                                    currentScreen = screen
                                    scope.launch { drawerState.close() }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = Color(0x1AFFFFFF),
                                    selectedTextColor = currentEmotionDef.color,
                                    selectedIconColor = currentEmotionDef.color,
                                    unselectedTextColor = TextSecondary,
                                    unselectedIconColor = TextMuted
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = currentScreen.title,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 20.sp,
                                    color = TextPrimary
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("app_menu_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu",
                                        tint = TextPrimary
                                    )
                                }
                            },
                            actions = {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(currentEmotionDef.color.copy(alpha = 0.15f))
                                        .border(1.dp, currentEmotionDef.color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = currentEmotionDef.name,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        color = currentEmotionDef.color
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = BgDark.copy(alpha = 0.85f),
                                titleContentColor = TextPrimary
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = BgDark.copy(alpha = 0.95f),
                            contentColor = TextSecondary,
                            tonalElevation = 0.dp,
                            modifier = Modifier.border(width = 0.8.dp, color = CardBorder)
                        ) {
                            val mainScreens = listOf(
                                Screen.HOME,
                                Screen.JOURNAL,
                                Screen.CHECK_IN,
                                Screen.HABITS,
                                Screen.TIMELINE
                            )

                            mainScreens.forEach { screen ->
                                val isSelected = currentScreen == screen
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentScreen = screen },
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = currentEmotionDef.color,
                                        selectedTextColor = currentEmotionDef.color,
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted,
                                        indicatorColor = Color(0x1AFFFFFF)
                                    ),
                                    modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                                )
                            }
                        }
                    },
                    containerColor = BgDark
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Ambient dynamic wave background
                        WaveBackgroundCanvas(
                            emotionDef = currentEmotionDef,
                            intensity = entries.firstOrNull()?.intensity ?: 50
                        )

                        // Screen content
                        when (currentScreen) {
                            Screen.HOME -> HomeScreen(
                                entries = entries,
                                streakCount = streakCount,
                                onNavigateToJournal = { currentScreen = Screen.JOURNAL },
                                onNavigateToCheckIn = { currentScreen = Screen.CHECK_IN },
                                onEntryClick = { selectedEntryForDetail = it }
                            )

                            Screen.JOURNAL -> JournalScreen(
                                entries = entries,
                                audioRecorderManager = audioRecorderManager,
                                onSaveEntry = { title, content, audioPath ->
                                    val analysis = EmotionEngine.analyze(content)
                                    val now = System.currentTimeMillis()
                                    val newEntry = JournalEntry(
                                        title = title,
                                        content = content,
                                        dominantEmotion = analysis.dominantEmotion,
                                        secondaryEmotions = analysis.secondaryEmotions.joinToString(","),
                                        intensity = analysis.intensity,
                                        confidence = analysis.confidence,
                                        sentiment = analysis.sentiment,
                                        themes = analysis.themes.joinToString(", "),
                                        cues = analysis.cues.joinToString(", "),
                                        audioPath = audioPath,
                                        createdAt = now
                                    )
                                    scope.launch {
                                        repository.insertEntry(newEntry)
                                        viewModel.trackJournalingStreak(now)
                                        Toast.makeText(context, "Moment preserved in your frequency", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onEntryClick = { selectedEntryForDetail = it }
                            )

                            Screen.CHECK_IN -> CheckInScreen(
                                onSaveCheckIn = { emotion, note ->
                                    val checkIn = DailyCheckIn(
                                        date = todayStr,
                                        emotion = emotion,
                                        note = note
                                    )
                                    scope.launch {
                                        repository.insertCheckIn(checkIn)
                                        Toast.makeText(context, "Check-in recorded", Toast.LENGTH_SHORT).show()
                                        currentScreen = Screen.HOME
                                    }
                                }
                            )

                            Screen.HABITS -> HabitsScreen(
                                todayHabits = todayHabits,
                                onToggleHabit = { habitName, isDone ->
                                    scope.launch {
                                        if (isDone) {
                                            repository.insertHabit(
                                                HabitItem(
                                                    date = todayStr,
                                                    habitName = habitName,
                                                    isCompleted = true
                                                )
                                            )
                                        } else {
                                            repository.removeHabit(todayStr, habitName)
                                        }
                                    }
                                }
                            )

                            Screen.TIMELINE -> TimelineScreen(
                                entries = entries,
                                onEntryClick = { selectedEntryForDetail = it }
                            )

                            Screen.REVIEWS -> ReviewsScreen(entries = entries)

                            Screen.WEATHER -> WeatherScreen()

                            Screen.CAPSULES -> CapsulesScreen(
                                capsules = capsules,
                                onSealCapsule = { content, unlockMillis ->
                                    scope.launch {
                                        repository.insertCapsule(
                                            TimeCapsule(
                                                content = content,
                                                unlockDateMillis = unlockMillis
                                            )
                                        )
                                        Toast.makeText(context, "Thought sealed in time", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )

                            Screen.EXERCISES -> ExercisesScreen()

                            Screen.WAVE_ART -> WaveArtScreen(currentEmotionKey = dominantEmotionKey)

                            Screen.ANALYZER -> AnalyzerScreen(
                                entries = entries,
                                onCorrectEmotion = { entryId, orig, corrected ->
                                    scope.launch {
                                        repository.insertCorrection(
                                            EmotionCorrection(
                                                entryId = entryId,
                                                originalEmotion = orig,
                                                correctedEmotion = corrected
                                            )
                                        )
                                        repository.updateEntryEmotion(entryId, corrected)
                                        Toast.makeText(context, "Calibration noted", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )

                            Screen.WAVE_TALK -> WaveTalkScreen()

                            Screen.SETTINGS -> SettingsScreen(
                                prefs = prefs,
                                entries = entries,
                                onAudioToggled = { isPlaying ->
                                    isSoundscapePlaying = isPlaying
                                },
                                onVolumeChanged = { SoundscapeEngine.setVolume(it) },
                                onAmbienceChanged = {
                                    currentAmbience = it
                                    SoundscapeEngine.setAmbience(it)
                                },
                                onRestoreBackup = {
                                    // Backup restored
                                }
                            )
                        }

                        // Floating audio dock
                        AudioPlayerDock(
                            isPlaying = isSoundscapePlaying,
                            ambienceName = currentAmbience,
                            onTogglePlay = {
                                isSoundscapePlaying = !isSoundscapePlaying
                                prefs.isProceduralAudioEnabled = isSoundscapePlaying
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.dp, bottom = 16.dp)
                        )
                    }
                }
            }
        }

        // Selected Entry Detail Modal
        selectedEntryForDetail?.let { entry ->
            EntryDetailDialog(
                entry = entry,
                isPlayingAudio = isPlayingVoiceNote,
                onPlayAudio = { path ->
                    audioRecorderManager.playAudio(path) {
                        isPlayingVoiceNote = false
                    }
                    isPlayingVoiceNote = true
                },
                onStopAudio = {
                    audioRecorderManager.stopAudio()
                    isPlayingVoiceNote = false
                },
                onToggleFavorite = { isFav ->
                    scope.launch {
                        repository.setFavorite(entry.id, isFav)
                        selectedEntryForDetail = entry.copy(isFavorite = isFav)
                    }
                },
                onDelete = {
                    scope.launch {
                        repository.deleteEntryById(entry.id)
                        selectedEntryForDetail = null
                        Toast.makeText(context, "Entry removed", Toast.LENGTH_SHORT).show()
                    }
                },
                onDismiss = {
                    audioRecorderManager.stopAudio()
                    isPlayingVoiceNote = false
                    selectedEntryForDetail = null
                }
            )
        }
    }
}
