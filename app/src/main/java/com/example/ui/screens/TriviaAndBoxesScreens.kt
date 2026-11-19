package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MysteryBoxItem
import com.example.model.TriviaCategory
import com.example.model.TriviaQuestion
import com.example.ui.components.Tactile3DButton
import com.example.ui.components.Tactile3DStyle
import com.example.ui.components.styleForThemeIndex
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.GoldLight
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TriviaGridSection(
    categories: List<TriviaCategory>,
    onSelectQuestion: (TriviaCategory, TriviaQuestion) -> Unit,
    onPickRandomQuestion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalQuestions = categories.sumOf { it.questions.size }
    val usedQuestions = categories.sumOf { cat -> cat.questions.count { it.isUsed } }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x661E293B))
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "🧠 شبكة التحدي المعرفي والدعم النفسي",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextMain
                )
                Text(
                    text = "اختاري فئة وقيمة النقاط ($usedQuestions / $totalQuestions مكتمل)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }

            Tactile3DButton(
                text = "🎲 سؤال عشوائي",
                onClick = onPickRandomQuestion,
                style = Tactile3DStyle.GOLD,
                fontSizeSp = 13,
                testTag = "random_question_button"
            )
        }

        // Adaptive 2 or 4 columns based on available screen width
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val columnsCount = if (maxWidth >= 700.dp) 4 else 2
            val rows = categories.chunked(columnsCount)

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                rows.forEach { rowCategories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowCategories.forEach { category ->
                            TriviaCategoryColumn(
                                category = category,
                                onSelectQuestion = { q -> onSelectQuestion(category, q) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowCategories.size < columnsCount) {
                            repeat(columnsCount - rowCategories.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TriviaCategoryColumn(
    category: TriviaCategory,
    onSelectQuestion: (TriviaQuestion) -> Unit,
    modifier: Modifier = Modifier
) {
    val style = styleForThemeIndex(category.themeIndex)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0x8C0F172A))
            .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(22.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 3D Category Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(style.topColor, style.bottomColor)
                    )
                )
                .border(1.dp, style.borderColor, RoundedCornerShape(16.dp))
                .padding(vertical = 12.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = category.emoji, fontSize = 24.sp)
                Text(
                    text = category.title,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = style.textColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Question Point Tiles
        category.questions.forEach { question ->
            val tileStyle = if (question.isUsed) Tactile3DStyle.USED else style
            val label = if (question.isUsed) "✓ ${question.points}" else "${question.points} ⭐"
            Tactile3DButton(
                text = label,
                onClick = { onSelectQuestion(question) },
                style = tileStyle,
                ledgeDepth = if (question.isUsed) 3.dp else 6.dp,
                cornerRadius = 18.dp,
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
                fontSizeSp = 20,
                modifier = Modifier.fillMaxWidth(),
                testTag = "tile_${question.id}"
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MysteryBoxesSection(
    boxes: List<MysteryBoxItem>,
    highlightedBoxNumber: Int?,
    isRouletteSpinning: Boolean,
    onSelectBox: (MysteryBoxItem) -> Unit,
    onSpinRoulette: () -> Unit,
    onResetBoxes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val remainingCount = boxes.count { !it.isOpened }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x661E293B))
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "🎁 صناديق المفاجآت والتمكين النفسي 3D",
                style = MaterialTheme.typography.titleLarge,
                color = TextMain
            )
            Text(
                text = "تحتوي الصناديق على تحديات بانتومايم المشاعر، رسائل امتنان، مواقف داعمة، ومفاجآت حماسية! (متبقي $remainingCount صندوق)",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Tactile3DButton(
                    text = if (isRouletteSpinning) "🎰 جاري السحب..." else "🎲 سحب عشوائي مثير",
                    onClick = onSpinRoulette,
                    enabled = !isRouletteSpinning,
                    style = Tactile3DStyle.GOLD,
                    fontSizeSp = 14,
                    testTag = "roulette_mystery_box_button"
                )
                Tactile3DButton(
                    text = "🔄 إعادة فتح الصناديق",
                    onClick = onResetBoxes,
                    enabled = !isRouletteSpinning,
                    style = Tactile3DStyle.GLASS,
                    fontSizeSp = 13,
                    testTag = "reset_mystery_boxes_button"
                )
            }
        }

        // Responsive Grid of 3D Mystery Boxes
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cols = when {
                maxWidth >= 820.dp -> 4
                maxWidth >= 540.dp -> 3
                else -> 2
            }
            val rows = boxes.chunked(cols)

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                rows.forEach { rowBoxes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowBoxes.forEach { box ->
                            MysteryBoxCard3D(
                                box = box,
                                isHighlighted = highlightedBoxNumber == box.number,
                                onClick = { onSelectBox(box) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowBoxes.size < cols) {
                            repeat(cols - rowBoxes.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MysteryBoxCard3D(
    box: MysteryBoxItem,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseStyle = when (box.number % 4) {
        1 -> Tactile3DStyle.PINK
        2 -> Tactile3DStyle.PURPLE
        3 -> Tactile3DStyle.TEAL
        else -> Tactile3DStyle.BLUE
    }
    val style = if (box.isOpened) Tactile3DStyle.USED else baseStyle
    val scale by animateFloatAsState(
        targetValue = if (isHighlighted) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "box_scale"
    )
    val ledgeDepth = if (box.isOpened) 3.dp else 7.dp
    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .testTag("mystery_box_${box.number}")
            .padding(bottom = ledgeDepth)
            .clickable(onClick = onClick)
    ) {
        // 3D Bottom Ledge
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = ledgeDepth)
                .clip(shape)
                .background(style.ledgeColor)
        )

        // 3D Box Face
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(style.topColor, style.bottomColor)
                    )
                )
                .border(
                    width = if (isHighlighted) 3.dp else 1.5.dp,
                    color = if (isHighlighted) GoldLight else style.borderColor,
                    shape = shape
                )
                .padding(vertical = 16.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (box.isOpened) "🔓" else box.emoji,
                fontSize = 36.sp
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x40000000))
                    .padding(horizontal = 14.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (box.isOpened) "مفتوح #${box.number}" else "صندوق #${box.number}",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            Text(
                text = "${box.points} نقطة",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (box.isOpened) TextMuted else GoldLight
            )
        }
    }
}
