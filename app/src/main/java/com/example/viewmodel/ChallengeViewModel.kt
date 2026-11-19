package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.MicrophoneEnergyMeter
import com.example.audio.SoundEffectsManager
import com.example.model.AffirmationCard
import com.example.model.BreathingPhase
import com.example.model.DefaultGameContent
import com.example.model.GameTab
import com.example.model.MysteryBoxItem
import com.example.model.MysteryType
import com.example.model.Team
import com.example.model.TriviaCategory
import com.example.model.TriviaQuestion
import com.example.model.WheelSlice
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max
import kotlin.random.Random

sealed class ActiveChallengeDialog {
    data class Trivia(
        val categoryTitle: String,
        val categoryEmoji: String,
        val question: TriviaQuestion
    ) : ActiveChallengeDialog()

    data class Mystery(
        val box: MysteryBoxItem
    ) : ActiveChallengeDialog()

    data class Wheel(
        val slice: WheelSlice
    ) : ActiveChallengeDialog()
}

data class ChallengeUiState(
    val teams: List<Team> = DefaultGameContent.defaultTeams(),
    val selectedTab: GameTab = GameTab.TRIVIA,
    val categories: List<TriviaCategory> = DefaultGameContent.defaultCategories(),
    val mysteryBoxes: List<MysteryBoxItem> = DefaultGameContent.defaultMysteryBoxes(),
    val wheelSlices: List<WheelSlice> = DefaultGameContent.defaultWheelSlices(),
    val affirmations: List<AffirmationCard> = DefaultGameContent.defaultAffirmations(),
    val currentAffirmationIndex: Int = 0,
    val isSoundEnabled: Boolean = true,
    val showQuickSoundboard: Boolean = false,
    // Active Challenge Modal state
    val activeDialog: ActiveChallengeDialog? = null,
    val isAnswerRevealed: Boolean = false,
    val isDoublePointsActive: Boolean = false,
    val timerSecondsLeft: Int = 30,
    val isTimerRunning: Boolean = false,
    // Mystery Box Roulette state
    val highlightedBoxNumber: Int? = null,
    val isBoxRouletteSpinning: Boolean = false,
    // Emotion Wheel state
    val wheelRotationDegrees: Float = 0f,
    val isWheelSpinning: Boolean = false,
    val lastSelectedSlice: WheelSlice? = null,
    // Applause & Energy Meter state
    val selectedMeterTeamId: Int = 1,
    val currentEnergyLevel: Float = 6f,
    val peakEnergyLevel: Float = 0f,
    val isMicListening: Boolean = false,
    val energyChallengeSecondsLeft: Int = 0,
    val cheerTapCount: Int = 0,
    // Calm Oasis Breathing state
    val isBreathingActive: Boolean = false,
    val currentBreathingPhase: BreathingPhase = BreathingPhase.INHALE,
    val breathingPhaseSecondsLeft: Int = BreathingPhase.INHALE.durationSeconds,
    val completedBreathingCycles: Int = 0,
    // Modals & Celebrations
    val showPodiumModal: Boolean = false,
    val showCustomizeModal: Boolean = false,
    val editingTeam: Team? = null,
    val confettiBurstId: Int = 0,
    val statusBannerMessage: String? = null
)

class ChallengeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("challenge_3d_prefs_v2", Context.MODE_PRIVATE)
    val soundManager = SoundEffectsManager(viewModelScope)
    val micMeter = MicrophoneEnergyMeter(viewModelScope)

    private val _uiState = MutableStateFlow(ChallengeUiState())
    val uiState: StateFlow<ChallengeUiState> = _uiState.asStateFlow()

    private var questionTimerJob: Job? = null
    private var energyDecayJob: Job? = null
    private var breathingJob: Job? = null

    init {
        loadSavedState()
        startEnergyDecayLoop()
    }

    fun selectTab(tab: GameTab) {
        soundManager.playClick()
        if (tab != GameTab.APPLAUSE_METER && _uiState.value.isMicListening) {
            stopMicMeter()
        }
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleSound() {
        val next = !_uiState.value.isSoundEnabled
        soundManager.isSoundEnabled = next
        if (next) soundManager.playClick()
        _uiState.update { it.copy(isSoundEnabled = next) }
        saveState()
    }

    fun toggleQuickSoundboard() {
        soundManager.playClick()
        _uiState.update { it.copy(showQuickSoundboard = !it.showQuickSoundboard) }
    }

    fun triggerSoundEffect(type: String) {
        when (type) {
            "fanfare" -> {
                soundManager.playAwardFanfare()
                triggerConfetti("🎺 تحية الفوز والتألق!")
            }
            "drumroll" -> {
                soundManager.playDrumrollEffect()
                showBanner("🥁 لحظة تشويق وحماس...")
            }
            "cheer" -> {
                soundManager.playCheerChord()
                triggerConfetti("👏 تصفيق حار للمشاركات!")
            }
            "chime" -> {
                soundManager.playBreathingChime()
                showBanner("✨ طاقة إيجابية وسكينة")
            }
        }
    }

    fun changeScore(teamId: Int, delta: Int) {
        if (delta > 0) {
            soundManager.playClick()
        } else {
            soundManager.playRouletteTick(0.8)
        }
        _uiState.update { state ->
            val updatedTeams = state.teams.map { team ->
                if (team.id == teamId) {
                    val newScore = max(0, team.score + delta)
                    val newStreak = if (delta > 0) team.streak + 1 else 0
                    team.copy(score = newScore, streak = newStreak)
                } else {
                    team
                }
            }
            state.copy(teams = updatedTeams)
        }
        saveState()
    }

    fun addExtraTeam() {
        val current = _uiState.value.teams
        if (current.size >= 4) return
        soundManager.playTileOpen()
        val nextId = (current.maxOfOrNull { it.id } ?: 2) + 1
        val presetName = if (current.size == 2) "فريق القوة" else "فريق الإشراق"
        val presetEmoji = if (current.size == 2) "💜" else "☀️"
        val newTeam = Team(
            id = nextId,
            name = presetName,
            emoji = presetEmoji,
            score = 0,
            streak = 0,
            themeIndex = current.size % 4
        )
        _uiState.update { it.copy(teams = it.teams + newTeam) }
        saveState()
    }

    fun removeLastTeam() {
        val current = _uiState.value.teams
        if (current.size <= 2) return
        soundManager.playClick()
        val updated = current.dropLast(1)
        _uiState.update { state ->
            state.copy(
                teams = updated,
                selectedMeterTeamId = updated.first().id
            )
        }
        saveState()
    }

    fun openEditTeamModal(team: Team) {
        soundManager.playClick()
        _uiState.update { it.copy(editingTeam = team) }
    }

    fun dismissEditTeamModal() {
        _uiState.update { it.copy(editingTeam = null) }
    }

    fun updateTeamDetails(teamId: Int, newName: String, newEmoji: String) {
        val cleanName = newName.trim().ifEmpty { "فريق $teamId" }
        soundManager.playClick()
        _uiState.update { state ->
            state.copy(
                teams = state.teams.map { t ->
                    if (t.id == teamId) t.copy(name = cleanName, emoji = newEmoji) else t
                },
                editingTeam = null
            )
        }
        saveState()
    }

    // --- Mode 1: Trivia Grid ---
    fun openTriviaQuestion(category: TriviaCategory, question: TriviaQuestion) {
        soundManager.playTileOpen()
        _uiState.update {
            it.copy(
                activeDialog = ActiveChallengeDialog.Trivia(
                    categoryTitle = category.title,
                    categoryEmoji = category.emoji,
                    question = question
                ),
                isAnswerRevealed = false,
                isDoublePointsActive = false,
                timerSecondsLeft = 30,
                isTimerRunning = true
            )
        }
        startQuestionCountdown()
    }

    fun pickRandomUnansweredQuestion() {
        val allUnused = _uiState.value.categories.flatMap { cat ->
            cat.questions.filter { !it.isUsed }.map { cat to it }
        }
        if (allUnused.isEmpty()) {
            showBanner("🎉 تم فتح جميع أسئلة الشبكة! يمكنكِ إعادة ضبط الجولة.")
            return
        }
        val (cat, q) = allUnused.random()
        openTriviaQuestion(cat, q)
    }

    // --- Mode 2: Mystery Boxes ---
    fun openMysteryBox(box: MysteryBoxItem) {
        if (_uiState.value.isBoxRouletteSpinning) return
        soundManager.playTileOpen()
        _uiState.update {
            it.copy(
                activeDialog = ActiveChallengeDialog.Mystery(box),
                isAnswerRevealed = false,
                isDoublePointsActive = false,
                timerSecondsLeft = 40,
                isTimerRunning = true,
                highlightedBoxNumber = box.number
            )
        }
        startQuestionCountdown()
    }

    fun spinRandomMysteryBox() {
        if (_uiState.value.isBoxRouletteSpinning) return
        val unopened = _uiState.value.mysteryBoxes.filter { !it.isOpened }
        if (unopened.isEmpty()) {
            showBanner("🎁 تم فتح جميع الصناديق! اضغطي «إعادة فتح الصناديق» للعب مجدداً.")
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isBoxRouletteSpinning = true) }
            val totalSteps = 16
            var chosen = unopened.random()
            for (step in 0 until totalSteps) {
                val candidate = unopened[step % unopened.size]
                chosen = if (step == totalSteps - 1) unopened.random() else candidate
                soundManager.playRouletteTick(0.85 + (step * 0.03))
                _uiState.update { it.copy(highlightedBoxNumber = chosen.number) }
                delay(65L + (step * 18L))
            }
            delay(220L)
            _uiState.update { it.copy(isBoxRouletteSpinning = false) }
            openMysteryBox(chosen)
        }
    }

    fun resetMysteryBoxesOnly() {
        soundManager.playClick()
        _uiState.update { state ->
            state.copy(
                mysteryBoxes = state.mysteryBoxes.map { it.copy(isOpened = false) },
                highlightedBoxNumber = null
            )
        }
        saveState()
        showBanner("🎁 تم إعادة تجهيز جميع صناديق المفاجآت!")
    }

    // --- Mode 3: Spinning 3D Wheel of Emotions ---
    fun spinEmotionWheel() {
        if (_uiState.value.isWheelSpinning) return
        val slices = _uiState.value.wheelSlices
        if (slices.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isWheelSpinning = true, lastSelectedSlice = null) }
            val extraSpins = Random.nextInt(4, 7) * 360f
            val randomAngle = Random.nextFloat() * 360f
            val startAngle = _uiState.value.wheelRotationDegrees
            val targetAngle = startAngle + extraSpins + randomAngle

            val steps = 38
            for (i in 1..steps) {
                val progress = i.toFloat() / steps
                // Ease-out cubic curve
                val eased = 1f - (1f - progress) * (1f - progress) * (1f - progress)
                val currentDeg = startAngle + (targetAngle - startAngle) * eased
                _uiState.update { it.copy(wheelRotationDegrees = currentDeg) }
                if (i % 2 == 0 || i > steps - 8) {
                    soundManager.playRouletteTick(0.9 + (1.0 - progress) * 0.35)
                }
                delay(45L + (i * 2L))
            }

            val normalizedAngle = ((360f - (targetAngle % 360f)) + 270f) % 360f
            val sliceAngle = 360f / slices.size
            val index = ((normalizedAngle / sliceAngle).toInt()) % slices.size
            val landedSlice = slices[index]

            soundManager.playTileOpen()
            _uiState.update {
                it.copy(
                    isWheelSpinning = false,
                    lastSelectedSlice = landedSlice,
                    activeDialog = ActiveChallengeDialog.Wheel(landedSlice),
                    isAnswerRevealed = false,
                    isDoublePointsActive = false,
                    timerSecondsLeft = 35,
                    isTimerRunning = true
                )
            }
            startQuestionCountdown()
        }
    }

    fun openMysteryOrWheelSlice(slice: WheelSlice) {
        soundManager.playTileOpen()
        _uiState.update {
            it.copy(
                lastSelectedSlice = slice,
                activeDialog = ActiveChallengeDialog.Wheel(slice),
                isAnswerRevealed = false,
                isDoublePointsActive = false,
                timerSecondsLeft = 35,
                isTimerRunning = true
            )
        }
        startQuestionCountdown()
    }

    // --- Active Challenge Modal Controls ---
    private fun startQuestionCountdown() {
        questionTimerJob?.cancel()
        questionTimerJob = viewModelScope.launch {
            while (isActive && _uiState.value.activeDialog != null) {
                delay(1000L)
                val state = _uiState.value
                if (state.isTimerRunning && state.timerSecondsLeft > 0) {
                    val nextSec = state.timerSecondsLeft - 1
                    if (nextSec in 1..5) {
                        soundManager.playCountdownTick(urgent = true)
                    }
                    _uiState.update { it.copy(timerSecondsLeft = nextSec) }
                }
            }
        }
    }

    fun toggleTimerPause() {
        soundManager.playClick()
        _uiState.update { it.copy(isTimerRunning = !it.isTimerRunning) }
    }

    fun addExtraTimerSeconds(extra: Int = 15) {
        soundManager.playClick()
        _uiState.update {
            it.copy(
                timerSecondsLeft = it.timerSecondsLeft + extra,
                isTimerRunning = true
            )
        }
    }

    fun toggleDoublePoints() {
        soundManager.playCheerChord()
        _uiState.update { it.copy(isDoublePointsActive = !it.isDoublePointsActive) }
    }

    fun revealAnswerOrGuidance() {
        soundManager.playRevealAnswer()
        _uiState.update {
            it.copy(
                isAnswerRevealed = true,
                isTimerRunning = false
            )
        }
    }

    fun awardActiveChallengeToTeam(teamId: Int?) {
        val currentDialog = _uiState.value.activeDialog ?: return
        val multiplier = if (_uiState.value.isDoublePointsActive) 2 else 1

        val basePoints = when (currentDialog) {
            is ActiveChallengeDialog.Trivia -> currentDialog.question.points
            is ActiveChallengeDialog.Mystery -> currentDialog.box.points
            is ActiveChallengeDialog.Wheel -> currentDialog.slice.points
        }
        val finalPoints = basePoints * multiplier

        questionTimerJob?.cancel()

        _uiState.update { state ->
            val updatedCategories = if (currentDialog is ActiveChallengeDialog.Trivia) {
                state.categories.map { cat ->
                    if (cat.id == currentDialog.question.categoryId) {
                        cat.copy(
                            questions = cat.questions.map { q ->
                                if (q.id == currentDialog.question.id) q.copy(isUsed = true) else q
                            }
                        )
                    } else cat
                }
            } else state.categories

            val updatedBoxes = if (currentDialog is ActiveChallengeDialog.Mystery) {
                state.mysteryBoxes.map { b ->
                    if (b.number == currentDialog.box.number) b.copy(isOpened = true) else b
                }
            } else state.mysteryBoxes

            val updatedTeams = if (teamId != null) {
                state.teams.map { t ->
                    if (t.id == teamId) {
                        t.copy(score = t.score + finalPoints, streak = t.streak + 1)
                    } else t
                }
            } else state.teams

            state.copy(
                categories = updatedCategories,
                mysteryBoxes = updatedBoxes,
                teams = updatedTeams,
                activeDialog = null,
                isAnswerRevealed = false,
                isDoublePointsActive = false,
                isTimerRunning = false
            )
        }

        if (teamId != null) {
            val winner = _uiState.value.teams.find { it.id == teamId }
            soundManager.playAwardFanfare()
            triggerConfetti("🎉 +$finalPoints نقطة لـ ${winner?.name ?: "الفريق"}!")
        } else {
            soundManager.playClick()
        }
        saveState()
    }

    fun awardBothTeamsFairPlay() {
        val currentDialog = _uiState.value.activeDialog ?: return
        val basePoints = when (currentDialog) {
            is ActiveChallengeDialog.Trivia -> currentDialog.question.points
            is ActiveChallengeDialog.Mystery -> currentDialog.box.points
            is ActiveChallengeDialog.Wheel -> currentDialog.slice.points
        }
        questionTimerJob?.cancel()

        _uiState.update { state ->
            val updatedCategories = if (currentDialog is ActiveChallengeDialog.Trivia) {
                state.categories.map { cat ->
                    cat.copy(
                        questions = cat.questions.map { q ->
                            if (q.id == currentDialog.question.id) q.copy(isUsed = true) else q
                        }
                    )
                }
            } else state.categories

            val updatedBoxes = if (currentDialog is ActiveChallengeDialog.Mystery) {
                state.mysteryBoxes.map { b ->
                    if (b.number == currentDialog.box.number) b.copy(isOpened = true) else b
                }
            } else state.mysteryBoxes

            state.copy(
                categories = updatedCategories,
                mysteryBoxes = updatedBoxes,
                teams = state.teams.map { t ->
                    t.copy(score = t.score + basePoints, streak = t.streak + 1)
                },
                activeDialog = null,
                isAnswerRevealed = false,
                isDoublePointsActive = false,
                isTimerRunning = false
            )
        }
        soundManager.playAwardFanfare()
        triggerConfetti("🤝 روح تعاون رائعة! +$basePoints نقطة لكل الفرق!")
        saveState()
    }

    fun dismissActiveDialogWithoutMarking() {
        questionTimerJob?.cancel()
        soundManager.playClick()
        _uiState.update {
            it.copy(
                activeDialog = null,
                isAnswerRevealed = false,
                isDoublePointsActive = false,
                isTimerRunning = false
            )
        }
    }

    // --- Mode 4: Applause & Energy Meter ---
    fun selectMeterTeam(teamId: Int) {
        soundManager.playClick()
        _uiState.update {
            it.copy(
                selectedMeterTeamId = teamId,
                currentEnergyLevel = 6f,
                peakEnergyLevel = 0f,
                cheerTapCount = 0
            )
        }
    }

    fun registerCheerTap() {
        soundManager.playRouletteTick(1.05 + (_uiState.value.currentEnergyLevel / 200.0))
        _uiState.update { state ->
            val boosted = (state.currentEnergyLevel + 11.5f).coerceAtMost(100f)
            val newPeak = max(state.peakEnergyLevel, boosted)
            state.copy(
                currentEnergyLevel = boosted,
                peakEnergyLevel = newPeak,
                cheerTapCount = state.cheerTapCount + 1
            )
        }
        if (_uiState.value.currentEnergyLevel >= 98f) {
            soundManager.playCheerChord()
        }
    }

    fun startMicMeter(context: Context) {
        if (!micMeter.hasMicPermission(context)) return
        _uiState.update { it.copy(isMicListening = true) }
        micMeter.startListening(
            context = context,
            onLevelNormalized = { micLevel ->
                _uiState.update { state ->
                    val blended = max(state.currentEnergyLevel, micLevel.coerceIn(5f, 100f))
                    val newPeak = max(state.peakEnergyLevel, blended)
                    state.copy(
                        currentEnergyLevel = blended,
                        peakEnergyLevel = newPeak
                    )
                }
            },
            onError = {
                _uiState.update { it.copy(isMicListening = false) }
            }
        )
    }

    fun stopMicMeter() {
        micMeter.stopListening()
        _uiState.update { it.copy(isMicListening = false) }
    }

    fun resetEnergyMeter() {
        soundManager.playClick()
        _uiState.update {
            it.copy(
                currentEnergyLevel = 6f,
                peakEnergyLevel = 0f,
                cheerTapCount = 0
            )
        }
    }

    fun awardPeakEnergyPoints() {
        val state = _uiState.value
        val peak = state.peakEnergyLevel.toInt()
        val bonusPoints = when {
            peak >= 90 -> 250
            peak >= 70 -> 200
            peak >= 45 -> 150
            peak >= 20 -> 100
            else -> 50
        }
        val targetTeam = state.teams.find { it.id == state.selectedMeterTeamId } ?: state.teams.first()
        changeScore(targetTeam.id, bonusPoints)
        soundManager.playAwardFanfare()
        triggerConfetti("👏 حماس مذهل (${peak}%)! +$bonusPoints نقطة لـ ${targetTeam.name}!")
        resetEnergyMeter()
    }

    private fun startEnergyDecayLoop() {
        energyDecayJob?.cancel()
        energyDecayJob = viewModelScope.launch {
            while (isActive) {
                delay(90L)
                val current = _uiState.value.currentEnergyLevel
                if (current > 6f) {
                    _uiState.update { state ->
                        state.copy(currentEnergyLevel = max(6f, state.currentEnergyLevel - 2.2f))
                    }
                }
            }
        }
    }

    // --- Mode 5: Calm Oasis (Guided Breathing & Affirmations) ---
    fun toggleBreathingExercise() {
        val nextActive = !_uiState.value.isBreathingActive
        if (nextActive) {
            soundManager.playBreathingChime()
            startBreathingLoop()
        } else {
            breathingJob?.cancel()
            _uiState.update {
                it.copy(
                    isBreathingActive = false,
                    currentBreathingPhase = BreathingPhase.INHALE,
                    breathingPhaseSecondsLeft = BreathingPhase.INHALE.durationSeconds
                )
            }
        }
    }

    private fun startBreathingLoop() {
        breathingJob?.cancel()
        breathingJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isBreathingActive = true,
                    currentBreathingPhase = BreathingPhase.INHALE,
                    breathingPhaseSecondsLeft = BreathingPhase.INHALE.durationSeconds
                )
            }
            while (isActive && _uiState.value.isBreathingActive) {
                delay(1000L)
                val state = _uiState.value
                if (!state.isBreathingActive) break
                if (state.breathingPhaseSecondsLeft > 1) {
                    _uiState.update {
                        it.copy(breathingPhaseSecondsLeft = it.breathingPhaseSecondsLeft - 1)
                    }
                } else {
                    val nextPhase = when (state.currentBreathingPhase) {
                        BreathingPhase.INHALE -> BreathingPhase.HOLD
                        BreathingPhase.HOLD -> BreathingPhase.EXHALE
                        BreathingPhase.EXHALE -> BreathingPhase.INHALE
                    }
                    val addedCycle = if (nextPhase == BreathingPhase.INHALE) 1 else 0
                    soundManager.playBreathingChime()
                    _uiState.update {
                        it.copy(
                            currentBreathingPhase = nextPhase,
                            breathingPhaseSecondsLeft = nextPhase.durationSeconds,
                            completedBreathingCycles = it.completedBreathingCycles + addedCycle
                        )
                    }
                }
            }
        }
    }

    fun nextAffirmationCard() {
        soundManager.playTileOpen()
        _uiState.update { state ->
            val nextIdx = (state.currentAffirmationIndex + 1) % state.affirmations.size
            state.copy(currentAffirmationIndex = nextIdx)
        }
    }

    fun previousAffirmationCard() {
        soundManager.playClick()
        _uiState.update { state ->
            val prevIdx = if (state.currentAffirmationIndex - 1 < 0) {
                state.affirmations.size - 1
            } else {
                state.currentAffirmationIndex - 1
            }
            state.copy(currentAffirmationIndex = prevIdx)
        }
    }

    // --- Podium, Editor & Reset ---
    fun openPodiumModal() {
        soundManager.playAwardFanfare()
        _uiState.update {
            it.copy(
                showPodiumModal = true,
                confettiBurstId = it.confettiBurstId + 1
            )
        }
    }

    fun closePodiumModal() {
        soundManager.playClick()
        _uiState.update { it.copy(showPodiumModal = false) }
    }

    fun openCustomizeModal() {
        soundManager.playClick()
        _uiState.update { it.copy(showCustomizeModal = true) }
    }

    fun closeCustomizeModal() {
        soundManager.playClick()
        _uiState.update { it.copy(showCustomizeModal = false) }
    }

    fun updateCustomQuestion(
        categoryId: Int,
        questionId: String,
        newCategoryTitle: String,
        newQuestionText: String,
        newAnswerText: String,
        newTipText: String
    ) {
        _uiState.update { state ->
            val updatedCats = state.categories.map { cat ->
                if (cat.id == categoryId) {
                    cat.copy(
                        title = newCategoryTitle.trim().ifEmpty { cat.title },
                        questions = cat.questions.map { q ->
                            if (q.id == questionId) {
                                q.copy(
                                    question = newQuestionText.trim().ifEmpty { q.question },
                                    answer = newAnswerText.trim().ifEmpty { q.answer },
                                    facilitatorTip = newTipText.trim().ifEmpty { q.facilitatorTip }
                                )
                            } else q
                        }
                    )
                } else cat
            }
            state.copy(categories = updatedCats)
        }
        saveState()
        showBanner("✅ تم حفظ تعديلات السؤال بنجاح")
    }

    fun updateCustomMysteryBox(
        boxNumber: Int,
        newPrompt: String,
        newGuidance: String,
        newPoints: Int
    ) {
        _uiState.update { state ->
            val updatedBoxes = state.mysteryBoxes.map { box ->
                if (box.number == boxNumber) {
                    box.copy(
                        prompt = newPrompt.trim().ifEmpty { box.prompt },
                        guidance = newGuidance.trim().ifEmpty { box.guidance },
                        points = newPoints.coerceIn(50, 1000)
                    )
                } else box
            }
            state.copy(mysteryBoxes = updatedBoxes)
        }
        saveState()
        showBanner("✅ تم تحديث الصندوق رقم $boxNumber")
    }

    fun resetCurrentRound() {
        soundManager.playTileOpen()
        _uiState.update { state ->
            state.copy(
                teams = state.teams.map { it.copy(score = 0, streak = 0) },
                categories = state.categories.map { cat ->
                    cat.copy(questions = cat.questions.map { it.copy(isUsed = false) })
                },
                mysteryBoxes = state.mysteryBoxes.map { it.copy(isOpened = false) },
                highlightedBoxNumber = null,
                currentEnergyLevel = 6f,
                peakEnergyLevel = 0f,
                cheerTapCount = 0
            )
        }
        saveState()
        showBanner("🔄 تم تصفير النقاط وإعادة فتح جميع الأسئلة والصناديق!")
    }

    fun restoreFactoryDefaults() {
        soundManager.playTileOpen()
        prefs.edit().clear().apply()
        _uiState.update {
            ChallengeUiState(
                isSoundEnabled = it.isSoundEnabled
            )
        }
        showBanner("🌸 تم استعادة المحتوى الافتراضي للدعم النفسي والاجتماعي!")
    }

    fun triggerConfetti(message: String? = null) {
        _uiState.update {
            it.copy(
                confettiBurstId = it.confettiBurstId + 1,
                statusBannerMessage = message ?: it.statusBannerMessage
            )
        }
        if (message != null) {
            clearBannerAfterDelay()
        }
    }

    fun showBanner(message: String) {
        _uiState.update { it.copy(statusBannerMessage = message) }
        clearBannerAfterDelay()
    }

    private fun clearBannerAfterDelay() {
        viewModelScope.launch {
            delay(3200L)
            _uiState.update { it.copy(statusBannerMessage = null) }
        }
    }

    // --- Persistence in SharedPreferences ---
    private fun saveState() {
        try {
            val state = _uiState.value
            val root = JSONObject()
            root.put("isSoundEnabled", state.isSoundEnabled)

            val teamsArr = JSONArray()
            for (t in state.teams) {
                teamsArr.put(
                    JSONObject().apply {
                        put("id", t.id)
                        put("name", t.name)
                        put("emoji", t.emoji)
                        put("score", t.score)
                        put("streak", t.streak)
                        put("themeIndex", t.themeIndex)
                    }
                )
            }
            root.put("teams", teamsArr)

            val catsArr = JSONArray()
            for (c in state.categories) {
                val qArr = JSONArray()
                for (q in c.questions) {
                    qArr.put(
                        JSONObject().apply {
                            put("id", q.id)
                            put("categoryId", q.categoryId)
                            put("points", q.points)
                            put("question", q.question)
                            put("answer", q.answer)
                            put("facilitatorTip", q.facilitatorTip)
                            put("isUsed", q.isUsed)
                        }
                    )
                }
                catsArr.put(
                    JSONObject().apply {
                        put("id", c.id)
                        put("title", c.title)
                        put("emoji", c.emoji)
                        put("themeIndex", c.themeIndex)
                        put("questions", qArr)
                    }
                )
            }
            root.put("categories", catsArr)

            val boxesArr = JSONArray()
            for (b in state.mysteryBoxes) {
                boxesArr.put(
                    JSONObject().apply {
                        put("number", b.number)
                        put("emoji", b.emoji)
                        put("type", b.type.name)
                        put("prompt", b.prompt)
                        put("guidance", b.guidance)
                        put("points", b.points)
                        put("isOpened", b.isOpened)
                    }
                )
            }
            root.put("mysteryBoxes", boxesArr)

            prefs.edit().putString("saved_game_json", root.toString()).apply()
        } catch (_: Throwable) {
        }
    }

    private fun loadSavedState() {
        try {
            val raw = prefs.getString("saved_game_json", null) ?: return
            val root = JSONObject(raw)
            val sound = root.optBoolean("isSoundEnabled", true)
            soundManager.isSoundEnabled = sound

            val loadedTeams = mutableListOf<Team>()
            val teamsArr = root.optJSONArray("teams")
            if (teamsArr != null) {
                for (i in 0 until teamsArr.length()) {
                    val obj = teamsArr.getJSONObject(i)
                    loadedTeams.add(
                        Team(
                            id = obj.getInt("id"),
                            name = obj.getString("name"),
                            emoji = obj.optString("emoji", "🌷"),
                            score = obj.getInt("score"),
                            streak = obj.optInt("streak", 0),
                            themeIndex = obj.optInt("themeIndex", i)
                        )
                    )
                }
            }

            val loadedCats = mutableListOf<TriviaCategory>()
            val catsArr = root.optJSONArray("categories")
            if (catsArr != null) {
                for (i in 0 until catsArr.length()) {
                    val cObj = catsArr.getJSONObject(i)
                    val qArr = cObj.getJSONArray("questions")
                    val qs = mutableListOf<TriviaQuestion>()
                    for (j in 0 until qArr.length()) {
                        val qObj = qArr.getJSONObject(j)
                        qs.add(
                            TriviaQuestion(
                                id = qObj.getString("id"),
                                categoryId = qObj.getInt("categoryId"),
                                points = qObj.getInt("points"),
                                question = qObj.getString("question"),
                                answer = qObj.getString("answer"),
                                facilitatorTip = qObj.optString("facilitatorTip", ""),
                                isUsed = qObj.optBoolean("isUsed", false)
                            )
                        )
                    }
                    loadedCats.add(
                        TriviaCategory(
                            id = cObj.getInt("id"),
                            title = cObj.getString("title"),
                            emoji = cObj.optString("emoji", "🌸"),
                            themeIndex = cObj.optInt("themeIndex", i),
                            questions = qs
                        )
                    )
                }
            }

            val loadedBoxes = mutableListOf<MysteryBoxItem>()
            val boxesArr = root.optJSONArray("mysteryBoxes")
            if (boxesArr != null) {
                for (i in 0 until boxesArr.length()) {
                    val bObj = boxesArr.getJSONObject(i)
                    val typeName = bObj.optString("type", MysteryType.PANTOMIME.name)
                    val mType = runCatching { MysteryType.valueOf(typeName) }
                        .getOrDefault(MysteryType.PANTOMIME)
                    loadedBoxes.add(
                        MysteryBoxItem(
                            number = bObj.getInt("number"),
                            emoji = bObj.optString("emoji", "🎁"),
                            type = mType,
                            prompt = bObj.getString("prompt"),
                            guidance = bObj.optString("guidance", ""),
                            points = bObj.optInt("points", 200),
                            isOpened = bObj.optBoolean("isOpened", false)
                        )
                    )
                }
            }

            _uiState.update { state ->
                state.copy(
                    isSoundEnabled = sound,
                    teams = loadedTeams.ifEmpty { state.teams },
                    categories = loadedCats.ifEmpty { state.categories },
                    mysteryBoxes = loadedBoxes.ifEmpty { state.mysteryBoxes }
                )
            }
        } catch (_: Throwable) {
        }
    }

    override fun onCleared() {
        super.onCleared()
        micMeter.stopListening()
        questionTimerJob?.cancel()
        energyDecayJob?.cancel()
        breathingJob?.cancel()
    }
}
