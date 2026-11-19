package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GameTab
import com.example.ui.components.ConfettiOverlayCanvas
import com.example.ui.components.CosmicBackgroundCanvas
import com.example.ui.components.ModeNavigationTabs
import com.example.ui.components.ScoreboardSection
import com.example.ui.components.TopHeaderBar
import com.example.ui.modals.ActiveChallengeModal
import com.example.ui.modals.CustomizeContentModal
import com.example.ui.modals.EditTeamModal
import com.example.ui.modals.PodiumCelebrationModal
import com.example.ui.screens.ApplauseMeterSection
import com.example.ui.screens.CalmOasisSection
import com.example.ui.screens.EmotionWheelSection
import com.example.ui.screens.MysteryBoxesSection
import com.example.ui.screens.TriviaGridSection
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ActiveChallengeDialog
import com.example.viewmodel.ChallengeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ChallengePlatformApp()
            }
        }
    }
}

@Composable
fun ChallengePlatformApp(
    viewModel: ChallengeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle back navigation from secondary game tabs back to main Trivia tab
    BackHandler(enabled = uiState.selectedTab != GameTab.TRIVIA) {
        viewModel.selectTab(GameTab.TRIVIA)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Animated 3D Cosmic Background
        CosmicBackgroundCanvas()

        // Main Scrollable Game Show Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 1140.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Header & Quick Facilitator Controls
                TopHeaderBar(
                    isSoundEnabled = uiState.isSoundEnabled,
                    showQuickSoundboard = uiState.showQuickSoundboard,
                    onToggleSound = viewModel::toggleSound,
                    onToggleSoundboard = viewModel::toggleQuickSoundboard,
                    onTriggerSoundEffect = viewModel::triggerSoundEffect,
                    onOpenCustomize = viewModel::openCustomizeModal,
                    onOpenPodium = viewModel::openPodiumModal,
                    onResetGame = viewModel::resetCurrentRound
                )

                // Teams Scoreboard
                ScoreboardSection(
                    teams = uiState.teams,
                    onChangeScore = viewModel::changeScore,
                    onEditTeam = viewModel::openEditTeamModal,
                    onAddTeam = viewModel::addExtraTeam,
                    onRemoveTeam = viewModel::removeLastTeam
                )

                // 5 Psychosocial Support & Entertainment Mode Tabs
                ModeNavigationTabs(
                    selectedTab = uiState.selectedTab,
                    onSelectTab = viewModel::selectTab
                )

                // Active Section Content
                AnimatedContent(
                    targetState = uiState.selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "game_mode_content"
                ) { tab ->
                    when (tab) {
                        GameTab.TRIVIA -> {
                            TriviaGridSection(
                                categories = uiState.categories,
                                onSelectQuestion = viewModel::openTriviaQuestion,
                                onPickRandomQuestion = viewModel::pickRandomUnansweredQuestion
                            )
                        }

                        GameTab.MYSTERY_BOXES -> {
                            MysteryBoxesSection(
                                boxes = uiState.mysteryBoxes,
                                highlightedBoxNumber = uiState.highlightedBoxNumber,
                                isRouletteSpinning = uiState.isBoxRouletteSpinning,
                                onSelectBox = viewModel::openMysteryBox,
                                onSpinRoulette = viewModel::spinRandomMysteryBox,
                                onResetBoxes = viewModel::resetMysteryBoxesOnly
                            )
                        }

                        GameTab.EMOTION_WHEEL -> {
                            EmotionWheelSection(
                                slices = uiState.wheelSlices,
                                rotationDegrees = uiState.wheelRotationDegrees,
                                isSpinning = uiState.isWheelSpinning,
                                onSpinWheel = viewModel::spinEmotionWheel,
                                onSelectSliceDirectly = { slice ->
                                    viewModel.soundManager.playTileOpen()
                                    viewModel.openMysteryOrWheelSlice(slice)
                                }
                            )
                        }

                        GameTab.APPLAUSE_METER -> {
                            ApplauseMeterSection(
                                teams = uiState.teams,
                                selectedTeamId = uiState.selectedMeterTeamId,
                                currentEnergy = uiState.currentEnergyLevel,
                                peakEnergy = uiState.peakEnergyLevel,
                                cheerTapCount = uiState.cheerTapCount,
                                isMicListening = uiState.isMicListening,
                                onSelectTeam = viewModel::selectMeterTeam,
                                onCheerTap = viewModel::registerCheerTap,
                                onStartMic = { viewModel.startMicMeter(context) },
                                onStopMic = viewModel::stopMicMeter,
                                onAwardPeakPoints = viewModel::awardPeakEnergyPoints,
                                onResetMeter = viewModel::resetEnergyMeter
                            )
                        }

                        GameTab.CALM_OASIS -> {
                            val currentAff = uiState.affirmations.getOrElse(uiState.currentAffirmationIndex) {
                                uiState.affirmations.first()
                            }
                            CalmOasisSection(
                                isBreathingActive = uiState.isBreathingActive,
                                currentPhase = uiState.currentBreathingPhase,
                                phaseSecondsLeft = uiState.breathingPhaseSecondsLeft,
                                completedCycles = uiState.completedBreathingCycles,
                                currentAffirmation = currentAff,
                                affirmationIndex = uiState.currentAffirmationIndex,
                                totalAffirmations = uiState.affirmations.size,
                                onToggleBreathing = viewModel::toggleBreathingExercise,
                                onNextAffirmation = viewModel::nextAffirmationCard,
                                onPrevAffirmation = viewModel::previousAffirmationCard,
                                onCelebrateCalmBonus = {
                                    uiState.teams.forEach { t ->
                                        viewModel.changeScore(t.id, 50)
                                    }
                                    viewModel.soundManager.playAwardFanfare()
                                    viewModel.triggerConfetti("🌸 +50 نقطة طمأنينة وتقدير لكل الفرق!")
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }

            // Floating Status Notification Banner
            AnimatedVisibility(
                visible = uiState.statusBannerMessage != null,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color(0xF01E1B4B))
                        .border(2.dp, GoldLight, RoundedCornerShape(99.dp))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = uiState.statusBannerMessage.orEmpty(),
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = GoldLight,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Full-Screen 3D Confetti Celebration Overlay
        ConfettiOverlayCanvas(burstId = uiState.confettiBurstId)

        // Active Challenge Modal (Trivia Question / Mystery Box / Wheel Slice)
        uiState.activeDialog?.let { activeDialog ->
            ActiveChallengeModal(
                dialogData = activeDialog,
                teams = uiState.teams,
                timerSecondsLeft = uiState.timerSecondsLeft,
                isTimerRunning = uiState.isTimerRunning,
                isAnswerRevealed = uiState.isAnswerRevealed,
                isDoublePointsActive = uiState.isDoublePointsActive,
                onToggleTimerPause = viewModel::toggleTimerPause,
                onAddExtraTime = { viewModel.addExtraTimerSeconds(15) },
                onToggleDoublePoints = viewModel::toggleDoublePoints,
                onRevealAnswer = viewModel::revealAnswerOrGuidance,
                onAwardTeam = viewModel::awardActiveChallengeToTeam,
                onAwardAllTeams = viewModel::awardBothTeamsFairPlay,
                onDismiss = viewModel::dismissActiveDialogWithoutMarking
            )
        }

        // Champions Podium Modal
        if (uiState.showPodiumModal) {
            PodiumCelebrationModal(
                teams = uiState.teams,
                onTriggerMoreConfetti = {
                    viewModel.soundManager.playAwardFanfare()
                    viewModel.triggerConfetti()
                },
                onClose = viewModel::closePodiumModal
            )
        }

        // Customize Content Editor Modal
        if (uiState.showCustomizeModal) {
            CustomizeContentModal(
                categories = uiState.categories,
                mysteryBoxes = uiState.mysteryBoxes,
                onSaveQuestion = viewModel::updateCustomQuestion,
                onSaveMysteryBox = viewModel::updateCustomMysteryBox,
                onRestoreDefaults = viewModel::restoreFactoryDefaults,
                onClose = viewModel::closeCustomizeModal
            )
        }

        // Edit Team Name & Emoji Modal
        uiState.editingTeam?.let { teamToEdit ->
            EditTeamModal(
                team = teamToEdit,
                onSave = viewModel::updateTeamDetails,
                onDismiss = viewModel::dismissEditTeamModal
            )
        }
    }
}
