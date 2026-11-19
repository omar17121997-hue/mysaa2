package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameTab
import com.example.model.Team
import com.example.ui.theme.BgGrad1
import com.example.ui.theme.BgGrad2
import com.example.ui.theme.BgGrad3
import com.example.ui.theme.BlueDark
import com.example.ui.theme.BlueDeepLedge
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BlueMain
import com.example.ui.theme.BorderGlass
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldDeepLedge
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMain
import com.example.ui.theme.PinkDark
import com.example.ui.theme.PinkDeepLedge
import com.example.ui.theme.PinkLight
import com.example.ui.theme.PinkMain
import com.example.ui.theme.PurpleDark
import com.example.ui.theme.PurpleDeepLedge
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurpleMain
import com.example.ui.theme.SurfaceCardDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealDeepLedge
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealMain
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class Tactile3DStyle(
    val topColor: Color,
    val bottomColor: Color,
    val ledgeColor: Color,
    val textColor: Color,
    val borderColor: Color
) {
    PURPLE(
        topColor = PurpleLight,
        bottomColor = PurpleMain,
        ledgeColor = PurpleDeepLedge,
        textColor = Color.White,
        borderColor = Color(0x55DDD6FE)
    ),
    PINK(
        topColor = PinkLight,
        bottomColor = PinkDark,
        ledgeColor = PinkDeepLedge,
        textColor = Color.White,
        borderColor = Color(0x55FBCFE8)
    ),
    GOLD(
        topColor = GoldLight,
        bottomColor = GoldMain,
        ledgeColor = GoldDeepLedge,
        textColor = Color(0xFF1E1B4B),
        borderColor = Color(0x88FEF08A)
    ),
    TEAL(
        topColor = TealLight,
        bottomColor = TealDark,
        ledgeColor = TealDeepLedge,
        textColor = Color(0xFF042F2E),
        borderColor = Color(0x6699F6E4)
    ),
    BLUE(
        topColor = BlueLight,
        bottomColor = BlueDark,
        ledgeColor = BlueDeepLedge,
        textColor = Color.White,
        borderColor = Color(0x55BFDBFE)
    ),
    GLASS(
        topColor = Color(0x28FFFFFF),
        bottomColor = Color(0x16FFFFFF),
        ledgeColor = Color(0xCC0F172A),
        textColor = TextMain,
        borderColor = BorderGlass
    ),
    USED(
        topColor = Color(0x55334155),
        bottomColor = Color(0x441E293B),
        ledgeColor = Color(0x880F172A),
        textColor = Color(0x55FFFFFF),
        borderColor = Color(0x18FFFFFF)
    )
}

fun styleForThemeIndex(index: Int): Tactile3DStyle = when (index % 4) {
    0 -> Tactile3DStyle.PINK
    1 -> Tactile3DStyle.PURPLE
    2 -> Tactile3DStyle.TEAL
    else -> Tactile3DStyle.GOLD
}

@Composable
fun Tactile3DButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Tactile3DStyle = Tactile3DStyle.PURPLE,
    enabled: Boolean = true,
    ledgeDepth: Dp = 5.dp,
    cornerRadius: Dp = 16.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
    fontSizeSp: Int = 14,
    testTag: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) (ledgeDepth - 1.dp).coerceAtLeast(0.dp) else 0.dp,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 700f),
        label = "btn3d_offset"
    )
    val shape = RoundedCornerShape(cornerRadius)
    val activeStyle = if (enabled) style else Tactile3DStyle.USED

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .padding(bottom = ledgeDepth)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // 3D Bottom Ledge
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = ledgeDepth)
                .clip(shape)
                .background(activeStyle.ledgeColor)
        )

        // 3D Top Tactile Surface
        Box(
            modifier = Modifier
                .offset(y = pressOffset)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(activeStyle.topColor, activeStyle.bottomColor)
                    )
                )
                .border(1.dp, activeStyle.borderColor, shape)
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = activeStyle.textColor,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = fontSizeSp.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CosmicBackgroundCanvas(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "cosmic_bg")
    val drift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_drift"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Base Deep Cosmic Gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(BgGrad3, BgGrad2, BgGrad1)
            )
        )

        // Glowing Nebula Orbs
        val w = size.width
        val h = size.height
        val orb1Center = Offset(
            x = w * (0.82f + 0.06f * cos(drift)),
            y = h * (0.14f + 0.04f * sin(drift))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x38EC4899), Color.Transparent),
                center = orb1Center,
                radius = w * 0.55f
            ),
            radius = w * 0.55f,
            center = orb1Center
        )

        val orb2Center = Offset(
            x = w * (0.18f + 0.06f * sin(drift)),
            y = h * (0.58f + 0.05f * cos(drift))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x338B5CF6), Color.Transparent),
                center = orb2Center,
                radius = w * 0.6f
            ),
            radius = w * 0.6f,
            center = orb2Center
        )

        val orb3Center = Offset(
            x = w * 0.75f,
            y = h * (0.85f + 0.04f * sin(drift * 1.5f))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x2814B8A6), Color.Transparent),
                center = orb3Center,
                radius = w * 0.5f
            ),
            radius = w * 0.5f,
            center = orb3Center
        )
    }
}

private data class ConfettiParticle(
    val xRatio: Float,
    val speedY: Float,
    val driftX: Float,
    val sizeDp: Float,
    val color: Color,
    val initialRotation: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiOverlayCanvas(
    burstId: Int,
    modifier: Modifier = Modifier
) {
    if (burstId <= 0) return

    val palette = remember {
        listOf(PinkLight, GoldLight, TealLight, PurpleLight, Color(0xFF38BDF8), Color(0xFF4ADE80))
    }
    val particles = remember(burstId) {
        List(75) {
            ConfettiParticle(
                xRatio = Random.nextFloat(),
                speedY = Random.nextFloat() * 0.65f + 0.45f,
                driftX = (Random.nextFloat() - 0.5f) * 0.28f,
                sizeDp = Random.nextFloat() * 10f + 7f,
                color = palette.random(),
                initialRotation = Random.nextFloat() * 360f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 520f
            )
        }
    }

    var elapsedSeconds by remember(burstId) { mutableFloatStateOf(0f) }

    LaunchedEffect(burstId) {
        val startNanos = withFrameNanos { it }
        while (elapsedSeconds < 2.8f) {
            val now = withFrameNanos { it }
            elapsedSeconds = (now - startNanos) / 1_000_000_000f
        }
    }

    if (elapsedSeconds < 2.8f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val progress = (elapsedSeconds / 2.8f).coerceIn(0f, 1f)
            val alpha = (1f - (progress * 0.85f)).coerceIn(0f, 1f)

            particles.forEach { p ->
                val px = (p.xRatio + p.driftX * elapsedSeconds) * size.width
                val py = (elapsedSeconds * p.speedY * size.height * 0.72f) - 20f
                if (py in -40f..size.height) {
                    rotate(
                        degrees = p.initialRotation + p.rotationSpeed * elapsedSeconds,
                        pivot = Offset(px, py)
                    ) {
                        drawRect(
                            color = p.color.copy(alpha = alpha),
                            topLeft = Offset(px - p.sizeDp, py - p.sizeDp * 0.6f),
                            size = Size(p.sizeDp * 1.8f, p.sizeDp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopHeaderBar(
    isSoundEnabled: Boolean,
    showQuickSoundboard: Boolean,
    onToggleSound: () -> Unit,
    onToggleSoundboard: () -> Unit,
    onTriggerSoundEffect: (String) -> Unit,
    onOpenCustomize: () -> Unit,
    onOpenPodium: () -> Unit,
    onResetGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderGlass, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 3D Lotus Badge
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .padding(bottom = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(y = 5.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PurpleDeepLedge)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PinkLight, PurpleMain)
                            )
                        )
                        .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🌸", fontSize = 26.sp)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "منصة التحدي والمرح 3D",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFFFDF2F8),
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "أنشطة تفاعلية للدعم النفسي الاجتماعي والتمكين",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
        }

        // Action Controls Group
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Tactile3DButton(
                text = if (isSoundEnabled) "🔊 الصوت: يعمل" else "🔇 الصوت: صامت",
                onClick = onToggleSound,
                style = Tactile3DStyle.GLASS,
                fontSizeSp = 13,
                testTag = "sound_toggle_button"
            )
            Tactile3DButton(
                text = "⚡ مؤثرات حماسية",
                onClick = onToggleSoundboard,
                style = if (showQuickSoundboard) Tactile3DStyle.PURPLE else Tactile3DStyle.GLASS,
                fontSizeSp = 13,
                testTag = "soundboard_toggle_button"
            )
            Tactile3DButton(
                text = "⚙️ تخصيص الفقرات",
                onClick = onOpenCustomize,
                style = Tactile3DStyle.GLASS,
                fontSizeSp = 13,
                testTag = "customize_questions_button"
            )
            Tactile3DButton(
                text = "🏆 التتويج",
                onClick = onOpenPodium,
                style = Tactile3DStyle.GOLD,
                fontSizeSp = 13,
                testTag = "podium_button"
            )
            Tactile3DButton(
                text = "🔄 إعادة ضبط",
                onClick = onResetGame,
                style = Tactile3DStyle.GLASS,
                fontSizeSp = 13,
                testTag = "reset_game_button"
            )
        }

        // Collapsible Live Facilitator Soundboard Strip
        AnimatedVisibility(
            visible = showQuickSoundboard,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x660F172A))
                    .border(1.dp, Color(0x28A78BFA), RoundedCornerShape(16.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🎵 لوحة المؤثرات المباشرة للميسّرة (لإشعال حماس القاعة):",
                    style = MaterialTheme.typography.labelMedium,
                    color = GoldLight
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Tactile3DButton(
                        text = "👏 تصفيق حار",
                        onClick = { onTriggerSoundEffect("cheer") },
                        style = Tactile3DStyle.PINK,
                        fontSizeSp = 12,
                        testTag = "sfx_cheer_btn"
                    )
                    Tactile3DButton(
                        text = "🥁 طبول التشويق",
                        onClick = { onTriggerSoundEffect("drumroll") },
                        style = Tactile3DStyle.PURPLE,
                        fontSizeSp = 12,
                        testTag = "sfx_drumroll_btn"
                    )
                    Tactile3DButton(
                        text = "🎺 بوق الفوز",
                        onClick = { onTriggerSoundEffect("fanfare") },
                        style = Tactile3DStyle.GOLD,
                        fontSizeSp = 12,
                        testTag = "sfx_fanfare_btn"
                    )
                    Tactile3DButton(
                        text = "✨ جرس السكينة",
                        onClick = { onTriggerSoundEffect("chime") },
                        style = Tactile3DStyle.TEAL,
                        fontSizeSp = 12,
                        testTag = "sfx_chime_btn"
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreboardSection(
    teams: List<Team>,
    onChangeScore: (Int, Int) -> Unit,
    onEditTeam: (Team) -> Unit,
    onAddTeam: () -> Unit,
    onRemoveTeam: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "لوحة نقاط الفرق المتنافسة (${teams.size} فرق)",
                style = MaterialTheme.typography.titleMedium,
                color = TextMuted
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (teams.size < 4) {
                    Tactile3DButton(
                        text = "+ إضافة فريق",
                        onClick = onAddTeam,
                        style = Tactile3DStyle.GLASS,
                        fontSizeSp = 12,
                        ledgeDepth = 3.dp,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        testTag = "add_team_button"
                    )
                }
                if (teams.size > 2) {
                    Tactile3DButton(
                        text = "- تقليص فريق",
                        onClick = onRemoveTeam,
                        style = Tactile3DStyle.GLASS,
                        fontSizeSp = 12,
                        ledgeDepth = 3.dp,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        testTag = "remove_team_button"
                    )
                }
            }
        }

        // Responsive 1 or 2 column layout for teams
        val chunkedTeams = teams.chunked(2)
        chunkedTeams.forEach { rowTeams ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowTeams.forEach { team ->
                    TeamScoreCard3D(
                        team = team,
                        onIncrement = { onChangeScore(team.id, 50) },
                        onDecrement = { onChangeScore(team.id, -50) },
                        onEdit = { onEditTeam(team) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowTeams.size == 1 && teams.size > 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TeamScoreCard3D(
    team: Team,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentBorder = when (team.themeIndex % 4) {
        0 -> Color(0x75F472B6)
        1 -> Color(0x752DD4BF)
        2 -> Color(0x75A78BFA)
        else -> Color(0x75FBBF24)
    }
    val avatarStyle = when (team.themeIndex % 4) {
        0 -> Tactile3DStyle.PINK
        1 -> Tactile3DStyle.TEAL
        2 -> Tactile3DStyle.PURPLE
        else -> Tactile3DStyle.GOLD
    }

    Box(
        modifier = modifier
            .testTag("team_card_${team.id}")
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xEE1E293B), Color(0xF50F172A))
                )
            )
            .border(2.dp, accentBorder, RoundedCornerShape(22.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEdit() }
                    .testTag("edit_team_${team.id}_row"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 3D Avatar Badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .padding(bottom = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .offset(y = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(avatarStyle.ledgeColor)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(avatarStyle.topColor, avatarStyle.bottomColor)
                                )
                            )
                            .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = team.emoji, fontSize = 22.sp)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = team.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextMain,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (team.streak >= 2) {
                            Text(
                                text = "🔥${team.streak}",
                                fontSize = 12.sp,
                                color = GoldLight,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0x33F59E0B))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "✏️ اضغطي لتعديل الاسم",
                        style = MaterialTheme.typography.labelMedium,
                        color = PinkLight.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }
            }

            // Score & +/- 50 Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Tactile3DButton(
                    text = "-50",
                    onClick = onDecrement,
                    style = Tactile3DStyle.GLASS,
                    ledgeDepth = 3.dp,
                    cornerRadius = 12.dp,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    fontSizeSp = 13,
                    testTag = "team_${team.id}_minus_btn"
                )

                Text(
                    text = "${team.score}",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    color = GoldLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("team_${team.id}_score_text")
                )

                Tactile3DButton(
                    text = "+50",
                    onClick = onIncrement,
                    style = avatarStyle,
                    ledgeDepth = 3.dp,
                    cornerRadius = 12.dp,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    fontSizeSp = 13,
                    testTag = "team_${team.id}_plus_btn"
                )
            }
        }
    }
}

@Composable
fun ModeNavigationTabs(
    selectedTab: GameTab,
    onSelectTab: (GameTab) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(GameTab.entries) { tab ->
            val isSelected = tab == selectedTab
            Tactile3DButton(
                text = "${tab.icon}  ${tab.title}",
                onClick = { onSelectTab(tab) },
                style = if (isSelected) Tactile3DStyle.PURPLE else Tactile3DStyle.GLASS,
                ledgeDepth = if (isSelected) 6.dp else 4.dp,
                cornerRadius = 18.dp,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                fontSizeSp = 15,
                testTag = "tab_${tab.name.lowercase()}"
            )
        }
    }
}
