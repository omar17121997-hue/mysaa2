package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AffirmationCard
import com.example.model.BreathingPhase
import com.example.model.Team
import com.example.model.WheelSlice
import com.example.ui.components.Tactile3DButton
import com.example.ui.components.Tactile3DStyle
import com.example.ui.theme.BorderGlass
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMain
import com.example.ui.theme.PinkDark
import com.example.ui.theme.PinkLight
import com.example.ui.theme.PinkMain
import com.example.ui.theme.PurpleDark
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurpleMain
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealMain
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmotionWheelSection(
    slices: List<WheelSlice>,
    rotationDegrees: Float,
    isSpinning: Boolean,
    onSpinWheel: () -> Unit,
    onSelectSliceDirectly: (WheelSlice) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderGlass, RoundedCornerShape(28.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "🎡 عجلة المشاعر والتنفيس الإبداعي 3D",
            style = MaterialTheme.typography.headlineMedium,
            color = TextMain,
            textAlign = TextAlign.Center
        )
        Text(
            text = "أديري العجلة لاختيار فقرة تعبير وجداني، كسر جليد، أو مشاركة داعمة بين الفرق!",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        // 3D Wheel Canvas + Center Spin Hub
        Box(
            modifier = Modifier
                .size(290.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            val sliceColors = listOf(
                PinkMain,
                PurpleMain,
                TealMain,
                GoldMain,
                Color(0xFF3B82F6),
                Color(0xFFEC4899),
                Color(0xFF10B981),
                Color(0xFFF97316)
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val diameter = min(size.width, size.height)
                val radius = diameter / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                // Outer 3D Shadow & Golden Rim
                drawCircle(
                    color = Color(0x99000000),
                    radius = radius,
                    center = Offset(center.x, center.y + 8f)
                )
                drawCircle(
                    brush = Brush.linearGradient(listOf(GoldLight, GoldDark)),
                    radius = radius,
                    center = center
                )

                val innerRadius = radius * 0.90f
                val sweep = 360f / slices.size.coerceAtLeast(1)

                rotate(degrees = rotationDegrees, pivot = center) {
                    slices.forEachIndexed { idx, _ ->
                        val startAngle = idx * sweep
                        drawArc(
                            color = sliceColors[idx % sliceColors.size],
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = true,
                            topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                            size = Size(innerRadius * 2f, innerRadius * 2f)
                        )
                        drawArc(
                            color = Color(0x44FFFFFF),
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = true,
                            topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                            size = Size(innerRadius * 2f, innerRadius * 2f),
                            style = Stroke(width = 3f)
                        )
                    }
                }

                // Glowing Rim Pegs
                val pegCount = 16
                for (i in 0 until pegCount) {
                    val angleRad = (i * (360.0 / pegCount)) * (PI / 180.0)
                    val pegX = center.x + (radius * 0.95f) * cos(angleRad).toFloat()
                    val pegY = center.y + (radius * 0.95f) * sin(angleRad).toFloat()
                    drawCircle(
                        color = Color.White,
                        radius = 4.5f,
                        center = Offset(pegX, pegY)
                    )
                }

                // Top Golden Indicator Pointer Arrow
                val pointerPath = Path().apply {
                    moveTo(center.x, center.y - innerRadius + 18f)
                    lineTo(center.x - 16f, center.y - radius - 6f)
                    lineTo(center.x + 16f, center.y - radius - 6f)
                    close()
                }
                drawPath(path = pointerPath, color = GoldLight)
                drawPath(path = pointerPath, color = Color(0xFF78350F), style = Stroke(width = 3f))
            }

            // Center 3D Hub
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(GoldLight, GoldDark)
                        )
                    )
                    .border(3.dp, Color.White, CircleShape)
                    .clickable(enabled = !isSpinning) { onSpinWheel() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSpinning) "✨" else "أديري\nالعجلة",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color(0xFF1E1B4B),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        Tactile3DButton(
            text = if (isSpinning) "🎡 العجلة تدور الآن..." else "🎡 تدوير عجلة المشاعر والمرح!",
            onClick = onSpinWheel,
            enabled = !isSpinning,
            style = Tactile3DStyle.GOLD,
            fontSizeSp = 16,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
            testTag = "spin_emotion_wheel_button"
        )

        // Slice Legend / Quick Direct Launch Pills
        Text(
            text = "أو اضغطي على أي فقرة من فقرات العجلة لفتحها مباشرة:",
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            slices.forEach { slice ->
                val pillStyle = when (slice.colorIndex % 4) {
                    0 -> Tactile3DStyle.PINK
                    1 -> Tactile3DStyle.PURPLE
                    2 -> Tactile3DStyle.TEAL
                    else -> Tactile3DStyle.GOLD
                }
                Tactile3DButton(
                    text = "${slice.emoji} ${slice.label} (${slice.points})",
                    onClick = { onSelectSliceDirectly(slice) },
                    style = pillStyle,
                    ledgeDepth = 4.dp,
                    fontSizeSp = 13,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    testTag = "wheel_slice_${slice.id}"
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApplauseMeterSection(
    teams: List<Team>,
    selectedTeamId: Int,
    currentEnergy: Float,
    peakEnergy: Float,
    cheerTapCount: Int,
    isMicListening: Boolean,
    onSelectTeam: (Int) -> Unit,
    onCheerTap: () -> Unit,
    onStartMic: () -> Unit,
    onStopMic: () -> Unit,
    onAwardPeakPoints: () -> Unit,
    onResetMeter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onStartMic()
        }
    }

    val animatedFill by animateFloatAsState(
        targetValue = (currentEnergy / 100f).coerceIn(0.06f, 1f),
        animationSpec = tween(durationMillis = 80),
        label = "gauge_fill"
    )

    val excitementTier = when {
        peakEnergy >= 90f -> "👑 قمة التفاعل الأسطوري! (+250 نقطة)"
        peakEnergy >= 70f -> "⚡ طاقة انفجارية رائعة! (+200 نقطة)"
        peakEnergy >= 45f -> "🔥 حماس متقد ومبهج! (+150 نقطة)"
        peakEnergy >= 20f -> "🌸 تفاعل جميل ودافئ! (+100 نقطة)"
        else -> "🌱 ابدؤوا التصفيق أو الضغط المتواصل لرفع المؤشر!"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderGlass, RoundedCornerShape(28.dp))
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "👏 مقياس الحماس والتصفيق الجماعي",
            style = MaterialTheme.typography.headlineMedium,
            color = TextMain,
            textAlign = TextAlign.Center
        )
        Text(
            text = "اختاري الفريق ثم صفّقوا بحرارة عبر الميكروفون أو اضغطوا سريعاً على زر التشجيع لشحن طاقة القاعة!",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        // Team Selection Pills
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            teams.forEach { team ->
                val isSelected = team.id == selectedTeamId
                Tactile3DButton(
                    text = "${team.emoji} ${team.name}",
                    onClick = { onSelectTeam(team.id) },
                    style = if (isSelected) Tactile3DStyle.GOLD else Tactile3DStyle.GLASS,
                    fontSizeSp = 14,
                    testTag = "meter_select_team_${team.id}"
                )
            }
        }

        // 3D Liquid Gauge Wrapper
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(CircleShape)
                .background(Color(0xD90F172A))
                .border(2.dp, Color(0x26FFFFFF), CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFill)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(TealMain, GoldMain, PinkMain)
                        )
                    )
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "${currentEnergy.toInt()}%",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }

        // Excitement Tier Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x33F59E0B))
                .border(1.dp, GoldLight.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = excitementTier,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = GoldLight,
                textAlign = TextAlign.Center
            )
        }

        // Meter Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "المستوى الحالي", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                Text(
                    text = "${currentEnergy.toInt()}%",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = TealLight
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "أعلى قمة حماس", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                Text(
                    text = "${peakEnergy.toInt()}%",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = GoldLight,
                    modifier = Modifier.testTag("peak_energy_text")
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "ضغطات التشجيع", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                Text(
                    text = "$cheerTapCount",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = PinkLight
                )
            }
        }

        // Giant Interactive 3D Cheer Drum Button
        Tactile3DButton(
            text = "🔥 اضغطي وهتفي بسرعة لرفع مقياس الحماس! 👏",
            onClick = onCheerTap,
            style = Tactile3DStyle.PINK,
            ledgeDepth = 8.dp,
            cornerRadius = 22.dp,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 18.dp),
            fontSizeSp = 17,
            modifier = Modifier.fillMaxWidth(),
            testTag = "cheer_tap_boost_button"
        )

        // Action Controls: Microphone Sensor + Award Points + Reset
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Tactile3DButton(
                text = if (isMicListening) "⏹️ إيقاف حساس الميكروفون" else "🎙️ قياس التصفيق بالميكروفون",
                onClick = {
                    if (isMicListening) {
                        onStopMic()
                    } else {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                style = if (isMicListening) Tactile3DStyle.TEAL else Tactile3DStyle.PURPLE,
                fontSizeSp = 13,
                testTag = "mic_toggle_button"
            )

            Tactile3DButton(
                text = "🏆 منح نقاط الحماس للفريق",
                onClick = onAwardPeakPoints,
                style = Tactile3DStyle.GOLD,
                fontSizeSp = 13,
                testTag = "award_energy_points_button"
            )

            Tactile3DButton(
                text = "🔄 تصفير المقياس",
                onClick = onResetMeter,
                style = Tactile3DStyle.GLASS,
                fontSizeSp = 13,
                testTag = "reset_energy_meter_button"
            )
        }
    }
}

@Composable
fun CalmOasisSection(
    isBreathingActive: Boolean,
    currentPhase: BreathingPhase,
    phaseSecondsLeft: Int,
    completedCycles: Int,
    currentAffirmation: AffirmationCard,
    affirmationIndex: Int,
    totalAffirmations: Int,
    onToggleBreathing: () -> Unit,
    onNextAffirmation: () -> Unit,
    onPrevAffirmation: () -> Unit,
    onCelebrateCalmBonus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetOrbScale = when {
        !isBreathingActive -> 0.82f
        currentPhase == BreathingPhase.INHALE -> 1.15f
        currentPhase == BreathingPhase.HOLD -> 1.15f
        else -> 0.75f
    }
    val orbScale by animateFloatAsState(
        targetValue = targetOrbScale,
        animationSpec = tween(durationMillis = if (isBreathingActive) currentPhase.durationSeconds * 950 else 500),
        label = "breathing_orb_scale"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Guided Group Breathing Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderGlass, RoundedCornerShape(28.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "🧘‍♀️ واحة التنفس الجماعي والاسترخاء (4-4-6)",
                style = MaterialTheme.typography.headlineMedium,
                color = TextMain,
                textAlign = TextAlign.Center
            )
            Text(
                text = "فقرة ختامية لتهدئة الجهاز العصبي واستعادة التوازن النفسي بعد الحماس والمسابقات",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 3D Glowing Breathing Lotus Orb
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(orbScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                TealLight.copy(alpha = 0.7f),
                                PurpleMain.copy(alpha = 0.55f),
                                Color(0x220F172A)
                            )
                        )
                    )
                    .border(3.dp, TealLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isBreathingActive) currentPhase.label else "🌸 تنفس جماعي",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    if (isBreathingActive) {
                        Text(
                            text = "$phaseSecondsLeft ث",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 30.sp,
                            color = GoldLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isBreathingActive) currentPhase.instruction else "اضغطي على زر البدء لتوجيه القاعة في دورة تنفس عميق ومريح",
                style = MaterialTheme.typography.bodyLarge,
                color = TealLight,
                textAlign = TextAlign.Center
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Tactile3DButton(
                    text = if (isBreathingActive) "⏹️ إيقاف التمرين" else "🌬️ بدء تمرين التنفس الجماعي",
                    onClick = onToggleBreathing,
                    style = if (isBreathingActive) Tactile3DStyle.PINK else Tactile3DStyle.TEAL,
                    fontSizeSp = 15,
                    testTag = "toggle_breathing_button"
                )
            }

            if (completedCycles > 0) {
                Text(
                    text = "✨ أتممتم $completedCycles دورة تنفس كاملة بنجاح!",
                    style = MaterialTheme.typography.labelLarge,
                    color = GoldLight
                )
            }
        }

        // Positive Psychosocial Affirmation Cards Deck
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xE6311042), Color(0xEE1E1B4B))
                    )
                )
                .border(1.5.dp, Color(0x55F472B6), RoundedCornerShape(28.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💌 بطاقات التوكيدات الإيجابية والتمكين",
                    style = MaterialTheme.typography.titleLarge,
                    color = PinkLight
                )
                Text(
                    text = "${affirmationIndex + 1} / $totalAffirmations",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Black,
                    color = GoldLight
                )
            }

            Text(text = currentAffirmation.emoji, fontSize = 42.sp)

            Text(
                text = currentAffirmation.title,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = GoldLight
            )

            Text(
                text = currentAffirmation.affirmationText,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x33000000))
                    .padding(16.dp)
            )

            Text(
                text = "💡 نشاط المجموعة: ${currentAffirmation.groupAction}",
                style = MaterialTheme.typography.bodyMedium,
                color = TealLight,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                Tactile3DButton(
                    text = "➡️ البطاقة السابقة",
                    onClick = onPrevAffirmation,
                    style = Tactile3DStyle.GLASS,
                    fontSizeSp = 13,
                    testTag = "prev_affirmation_btn"
                )
                Tactile3DButton(
                    text = "🌟 مكافأة التوكيد الجماعي (+50)",
                    onClick = onCelebrateCalmBonus,
                    style = Tactile3DStyle.GOLD,
                    fontSizeSp = 13,
                    testTag = "affirmation_bonus_btn"
                )
                Tactile3DButton(
                    text = "البطاقة التالية ⬅️",
                    onClick = onNextAffirmation,
                    style = Tactile3DStyle.PURPLE,
                    fontSizeSp = 13,
                    testTag = "next_affirmation_btn"
                )
            }
        }
    }
}
