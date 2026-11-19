package com.example.ui.modals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.DefaultGameContent
import com.example.model.MysteryBoxItem
import com.example.model.Team
import com.example.model.TriviaCategory
import com.example.ui.components.Tactile3DButton
import com.example.ui.components.Tactile3DStyle
import com.example.ui.components.styleForThemeIndex
import com.example.ui.theme.BgGrad1
import com.example.ui.theme.BgGrad2
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMain
import com.example.ui.theme.PinkLight
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurpleMain
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealMain
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted
import com.example.viewmodel.ActiveChallengeDialog

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveChallengeModal(
    dialogData: ActiveChallengeDialog,
    teams: List<Team>,
    timerSecondsLeft: Int,
    isTimerRunning: Boolean,
    isAnswerRevealed: Boolean,
    isDoublePointsActive: Boolean,
    onToggleTimerPause: () -> Unit,
    onAddExtraTime: () -> Unit,
    onToggleDoublePoints: () -> Unit,
    onRevealAnswer: () -> Unit,
    onAwardTeam: (Int?) -> Unit,
    onAwardAllTeams: () -> Unit,
    onDismiss: () -> Unit
) {
    val multiplier = if (isDoublePointsActive) 2 else 1

    val badgeText: String
    val promptText: String
    val answerOrGuidanceTitle: String
    val answerOrGuidanceText: String
    val facilitatorTip: String
    val basePoints: Int

    when (dialogData) {
        is ActiveChallengeDialog.Trivia -> {
            badgeText = "${dialogData.categoryEmoji} ${dialogData.categoryTitle} • ${dialogData.question.points * multiplier} نقطة"
            promptText = dialogData.question.question
            answerOrGuidanceTitle = "✅ الإجابة النموذجية:"
            answerOrGuidanceText = dialogData.question.answer
            facilitatorTip = dialogData.question.facilitatorTip
            basePoints = dialogData.question.points
        }
        is ActiveChallengeDialog.Mystery -> {
            badgeText = "صندوق #${dialogData.box.number} • ${dialogData.box.type.badgeTitle} • ${dialogData.box.points * multiplier} نقطة"
            promptText = dialogData.box.prompt
            answerOrGuidanceTitle = "🌸 الرسالة النفسية والاجتماعية:"
            answerOrGuidanceText = dialogData.box.guidance
            facilitatorTip = "شجعي المشاركات على التفاعل العفوي والتصفيق لبعضهن بعد أداء التحدي."
            basePoints = dialogData.box.points
        }
        is ActiveChallengeDialog.Wheel -> {
            badgeText = "🎡 عجلة المشاعر • ${dialogData.slice.emoji} ${dialogData.slice.label} • ${dialogData.slice.points * multiplier} نقطة"
            promptText = dialogData.slice.prompt
            answerOrGuidanceTitle = "💡 إضاءة التمكين النفسي:"
            answerOrGuidanceText = dialogData.slice.facilitatorNote
            facilitatorTip = "امنحي المساحة للمشاركة دون استعجال واحتفي بكل فكرة تُطرح."
            basePoints = dialogData.slice.points
        }
    }

    val finalPoints = basePoints * multiplier

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                )
                .border(2.dp, Color(0x38FFFFFF), RoundedCornerShape(28.dp))
                .padding(22.dp)
                .testTag("active_challenge_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Modal Badge
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0x408B5CF6))
                        .border(1.dp, PurpleLight, CircleShape)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFDDD6FE),
                        textAlign = TextAlign.Center
                    )
                }

                // Timer Ring & Power-Up Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Tactile3DButton(
                        text = if (isTimerRunning) "⏸️ إيقاف مؤقت" else "▶️ استئناف",
                        onClick = onToggleTimerPause,
                        style = Tactile3DStyle.GLASS,
                        ledgeDepth = 3.dp,
                        fontSizeSp = 12,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        testTag = "timer_pause_btn"
                    )

                    // Circular Timer Ring
                    val ringColor = if (timerSecondsLeft <= 5) PinkLight else GoldLight
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0x33000000))
                            .border(4.dp, ringColor, CircleShape)
                            .clickable { onToggleTimerPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$timerSecondsLeft",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = ringColor
                        )
                    }

                    Tactile3DButton(
                        text = "+15 ثانية",
                        onClick = onAddExtraTime,
                        style = Tactile3DStyle.GLASS,
                        ledgeDepth = 3.dp,
                        fontSizeSp = 12,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        testTag = "timer_add_btn"
                    )
                }

                // Double Points Excitement Power-up Button
                Tactile3DButton(
                    text = if (isDoublePointsActive) "🔥 النقاط مضاعفة الآن (x2 = $finalPoints نقطة)!" else "⚡ تفعيل مضاعفة النقاط الحماسية (x2)",
                    onClick = onToggleDoublePoints,
                    style = if (isDoublePointsActive) Tactile3DStyle.GOLD else Tactile3DStyle.GLASS,
                    ledgeDepth = 3.dp,
                    fontSizeSp = 12,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    testTag = "double_points_toggle_btn"
                )

                // Question / Challenge Main Text
                Text(
                    text = promptText,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 21.sp,
                    lineHeight = 32.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("modal_question_text")
                )

                // Facilitator Tip Card
                if (facilitatorTip.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x1AF472B6))
                            .border(1.dp, Color(0x44F472B6), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "💡 إضاءة للميسّرة: $facilitatorTip",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PinkLight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Reveal Answer Button or Revealed Answer Box
                if (!isAnswerRevealed) {
                    Tactile3DButton(
                        text = "✨ إظهار الإجابة والتوجيه النفسي",
                        onClick = onRevealAnswer,
                        style = Tactile3DStyle.TEAL,
                        fontSizeSp = 15,
                        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
                        testTag = "reveal_answer_button"
                    )
                }

                AnimatedVisibility(
                    visible = isAnswerRevealed,
                    enter = expandVertically() + fadeIn()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0x2914B8A6))
                            .border(1.5.dp, Color(0x752DD4BF), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                            .testTag("modal_answer_box"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = answerOrGuidanceTitle,
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = GoldLight
                        )
                        Text(
                            text = answerOrGuidanceText,
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            lineHeight = 26.sp,
                            color = Color(0xFF99F6E4),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "منح نقاط التحدي (+$finalPoints نقطة):",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextMuted
                )

                // Team Award Buttons
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    teams.forEach { team ->
                        Tactile3DButton(
                            text = "${team.emoji} +$finalPoints ${team.name}",
                            onClick = { onAwardTeam(team.id) },
                            style = styleForThemeIndex(team.themeIndex),
                            fontSizeSp = 14,
                            testTag = "award_team_${team.id}_button"
                        )
                    }
                }

                // Secondary Actions: Fair Play Both Teams / Mark Used Without Points / Cancel
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Tactile3DButton(
                        text = "🤝 منح النقاط لكل الفرق",
                        onClick = onAwardAllTeams,
                        style = Tactile3DStyle.GOLD,
                        fontSizeSp = 12,
                        testTag = "award_all_teams_button"
                    )
                    Tactile3DButton(
                        text = "✓ إتمام بدون نقاط",
                        onClick = { onAwardTeam(null) },
                        style = Tactile3DStyle.GLASS,
                        fontSizeSp = 12,
                        testTag = "complete_no_points_button"
                    )
                    Tactile3DButton(
                        text = "✖ إغلاق",
                        onClick = onDismiss,
                        style = Tactile3DStyle.GLASS,
                        fontSizeSp = 12,
                        testTag = "close_challenge_modal_button"
                    )
                }
            }
        }
    }
}

@Composable
fun PodiumCelebrationModal(
    teams: List<Team>,
    onTriggerMoreConfetti: () -> Unit,
    onClose: () -> Unit
) {
    val sortedTeams = remember(teams) { teams.sortedByDescending { it.score } }
    val topScore = sortedTeams.firstOrNull()?.score ?: 0
    val isTie = sortedTeams.size >= 2 && sortedTeams[0].score == sortedTeams[1].score

    val psychosocialBadges = listOf(
        "👑 وسام التميز والطاقة الإيجابية",
        "🤝 وسام التساند والروح الجماعية",
        "🌸 وسام الإبداع والمرونة النفسية",
        "🌟 وسام البهجة والتفاعل المشرق"
    )

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF311042), Color(0xFF1E1B4B), Color(0xFF0F172A))
                    )
                )
                .border(2.dp, GoldLight, RoundedCornerShape(28.dp))
                .padding(24.dp)
                .testTag("podium_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(text = "🏆", fontSize = 54.sp)

                Text(
                    text = if (isTie && topScore > 0) {
                        "🎉 تعادل بطولي مشترك في القمة!"
                    } else {
                        "🎉 منصة التتويج والاحتفال!"
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = GoldLight,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "كل فريق اليوم فائز بروح التعاون، المساندة النفسية، والطاقة الجميلة التي شاركتموها!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                // Ranked Podium Cards
                sortedTeams.forEachIndexed { index, team ->
                    val medal = when (index) {
                        0 -> "🥇 المركز الأول"
                        1 -> "🥈 المركز الثاني"
                        2 -> "🥉 المركز الثالث"
                        else -> "🎖️ المركز الرابع"
                    }
                    val badge = psychosocialBadges[index % psychosocialBadges.size]
                    val cardBorder = if (index == 0) GoldLight else Color(0x38FFFFFF)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (index == 0) Color(0x33F59E0B) else Color(0x661E293B)
                            )
                            .border(2.dp, cardBorder, RoundedCornerShape(20.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = team.emoji, fontSize = 28.sp)
                                Column {
                                    Text(
                                        text = "$medal • ${team.name}",
                                        fontFamily = CairoFontFamily,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = badge,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PinkLight
                                    )
                                }
                            }

                            Text(
                                text = "${team.score} نقطة",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                color = GoldLight
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Tactile3DButton(
                        text = "🎊 إطلاق قصاصات الفرح",
                        onClick = onTriggerMoreConfetti,
                        style = Tactile3DStyle.PINK,
                        fontSizeSp = 14,
                        testTag = "podium_confetti_btn"
                    )
                    Tactile3DButton(
                        text = "تم وإغلاق",
                        onClick = onClose,
                        style = Tactile3DStyle.GOLD,
                        fontSizeSp = 14,
                        testTag = "close_podium_btn"
                    )
                }
            }
        }
    }
}

@Composable
fun CustomizeContentModal(
    categories: List<TriviaCategory>,
    mysteryBoxes: List<MysteryBoxItem>,
    onSaveQuestion: (Int, String, String, String, String, String) -> Unit,
    onSaveMysteryBox: (Int, String, String, Int) -> Unit,
    onRestoreDefaults: () -> Unit,
    onClose: () -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Trivia, 1 = Mystery Boxes
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 650.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                )
                .border(2.dp, Color(0x38FFFFFF), RoundedCornerShape(28.dp))
                .padding(20.dp)
                .testTag("customize_content_modal")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚙️ تخصيص الأسئلة وفقرات الدعم النفسي",
                        style = MaterialTheme.typography.titleLarge,
                        color = GoldLight
                    )
                    Tactile3DButton(
                        text = "✖ إغلاق",
                        onClick = onClose,
                        style = Tactile3DStyle.GLASS,
                        ledgeDepth = 3.dp,
                        fontSizeSp = 12,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        testTag = "close_customize_modal_btn"
                    )
                }

                // Sub-tabs: Trivia vs Mystery Boxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Tactile3DButton(
                        text = "🧠 أسئلة الشبكة",
                        onClick = { activeTab = 0 },
                        style = if (activeTab == 0) Tactile3DStyle.PURPLE else Tactile3DStyle.GLASS,
                        modifier = Modifier.weight(1f),
                        fontSizeSp = 13,
                        testTag = "editor_tab_trivia"
                    )
                    Tactile3DButton(
                        text = "🎁 صناديق المفاجآت",
                        onClick = { activeTab = 1 },
                        style = if (activeTab == 1) Tactile3DStyle.PINK else Tactile3DStyle.GLASS,
                        modifier = Modifier.weight(1f),
                        fontSizeSp = 13,
                        testTag = "editor_tab_boxes"
                    )
                }

                if (activeTab == 0) {
                    // Category selector pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEachIndexed { idx, cat ->
                            Tactile3DButton(
                                text = "${cat.emoji} ${cat.title}",
                                onClick = { selectedCategoryIndex = idx },
                                style = if (selectedCategoryIndex == idx) Tactile3DStyle.TEAL else Tactile3DStyle.GLASS,
                                ledgeDepth = 3.dp,
                                fontSizeSp = 11,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    val currentCat = categories.getOrNull(selectedCategoryIndex) ?: categories.first()
                    var catTitle by remember(currentCat.id) { mutableStateOf(currentCat.title) }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            OutlinedTextField(
                                value = catTitle,
                                onValueChange = { catTitle = it },
                                label = { Text("عنوان الفئة ${currentCat.emoji}") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = editorTextFieldColors()
                            )
                        }

                        items(currentCat.questions, key = { it.id }) { q ->
                            var qText by remember(q.id, q.question) { mutableStateOf(q.question) }
                            var aText by remember(q.id, q.answer) { mutableStateOf(q.answer) }
                            var tipText by remember(q.id, q.facilitatorTip) { mutableStateOf(q.facilitatorTip) }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0x14FFFFFF))
                                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "سؤال بقيمة ${q.points} نقطة",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = PinkLight,
                                    fontSize = 14.sp
                                )
                                OutlinedTextField(
                                    value = qText,
                                    onValueChange = { qText = it },
                                    label = { Text("نص السؤال أو التحدي") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = editorTextFieldColors()
                                )
                                OutlinedTextField(
                                    value = aText,
                                    onValueChange = { aText = it },
                                    label = { Text("الإجابة أو الرسالة الداعمة") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = editorTextFieldColors()
                                )
                                OutlinedTextField(
                                    value = tipText,
                                    onValueChange = { tipText = it },
                                    label = { Text("إضاءة للميسّرة") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = editorTextFieldColors()
                                )
                                Tactile3DButton(
                                    text = "💾 حفظ هذا السؤال",
                                    onClick = {
                                        onSaveQuestion(
                                            currentCat.id,
                                            q.id,
                                            catTitle,
                                            qText,
                                            aText,
                                            tipText
                                        )
                                    },
                                    style = Tactile3DStyle.TEAL,
                                    ledgeDepth = 3.dp,
                                    fontSizeSp = 12
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(mysteryBoxes, key = { it.number }) { box ->
                            var pText by remember(box.number, box.prompt) { mutableStateOf(box.prompt) }
                            var gText by remember(box.number, box.guidance) { mutableStateOf(box.guidance) }
                            var ptsText by remember(box.number, box.points) { mutableStateOf(box.points.toString()) }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0x14FFFFFF))
                                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${box.emoji} صندوق رقم #${box.number} (${box.type.badgeTitle})",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight,
                                    fontSize = 14.sp
                                )
                                OutlinedTextField(
                                    value = pText,
                                    onValueChange = { pText = it },
                                    label = { Text("تحدي الصندوق") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = editorTextFieldColors()
                                )
                                OutlinedTextField(
                                    value = gText,
                                    onValueChange = { gText = it },
                                    label = { Text("الحكمة أو التوجيه النفسي") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = editorTextFieldColors()
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = ptsText,
                                        onValueChange = { ptsText = it.filter { ch -> ch.isDigit() } },
                                        label = { Text("النقاط") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = editorTextFieldColors()
                                    )
                                    Tactile3DButton(
                                        text = "💾 حفظ الصندوق",
                                        onClick = {
                                            val pts = ptsText.toIntOrNull() ?: box.points
                                            onSaveMysteryBox(box.number, pText, gText, pts)
                                        },
                                        style = Tactile3DStyle.GOLD,
                                        ledgeDepth = 3.dp,
                                        fontSizeSp = 12
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Restore Defaults button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Tactile3DButton(
                        text = "🌸 استعادة المحتوى الأصلي",
                        onClick = onRestoreDefaults,
                        style = Tactile3DStyle.GLASS,
                        ledgeDepth = 3.dp,
                        fontSizeSp = 12,
                        testTag = "restore_defaults_button"
                    )
                    Tactile3DButton(
                        text = "✅ إغلاق المحرر",
                        onClick = onClose,
                        style = Tactile3DStyle.PURPLE,
                        ledgeDepth = 4.dp,
                        fontSizeSp = 13
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditTeamModal(
    team: Team,
    onSave: (Int, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var nameInput by remember(team.id) { mutableStateOf(team.name) }
    var selectedEmoji by remember(team.id) { mutableStateOf(team.emoji) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                )
                .border(2.dp, Color(0x38FFFFFF), RoundedCornerShape(24.dp))
                .padding(20.dp)
                .testTag("edit_team_modal"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "✏️ تعديل اسم وشعار الفريق",
                style = MaterialTheme.typography.titleLarge,
                color = GoldLight
            )

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("اسم الفريق") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("team_name_input"),
                colors = editorTextFieldColors()
            )

            Text(
                text = "اختاري الرمز التعبيري للفريق:",
                style = MaterialTheme.typography.labelLarge,
                color = TextMuted
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DefaultGameContent.availableTeamEmojis.forEach { emoji ->
                    val isChosen = emoji == selectedEmoji
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isChosen) Color(0x44F59E0B) else Color(0x18FFFFFF))
                            .border(
                                width = if (isChosen) 2.dp else 1.dp,
                                color = if (isChosen) GoldLight else Color(0x22FFFFFF),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedEmoji = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 22.sp)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                Tactile3DButton(
                    text = "💾 حفظ التعديلات",
                    onClick = { onSave(team.id, nameInput, selectedEmoji) },
                    style = Tactile3DStyle.GOLD,
                    fontSizeSp = 14,
                    testTag = "save_team_edit_btn"
                )
                Tactile3DButton(
                    text = "إلغاء",
                    onClick = onDismiss,
                    style = Tactile3DStyle.GLASS,
                    fontSizeSp = 14
                )
            }
        }
    }
}

@Composable
private fun editorTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextMain,
    unfocusedTextColor = TextMain,
    focusedContainerColor = Color(0xAA0F172A),
    unfocusedContainerColor = Color(0x770F172A),
    focusedBorderColor = GoldLight,
    unfocusedBorderColor = Color(0x38FFFFFF),
    focusedLabelColor = GoldLight,
    unfocusedLabelColor = TextMuted
)
