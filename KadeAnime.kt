/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * Kade'X MC is an unofficial modified version of Zalith Launcher 2.
 * Kade'X MC modifications are maintained by StrykeX.
 * The upstream copyright and the GPL-3.0 license remain in force.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.content.elements

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Kade'X MC：动漫风格（Anime Style）配色
 */
object KadeAnime {
    val Sakura = Color(0xFFFF6FB1)
    val Violet = Color(0xFF8B6CFF)
    val Cyan = Color(0xFF46D9FF)
    val Gold = Color(0xFFFFD86B)
    val Orange = Color(0xFFFFB347)
    val Outline = Color(0xFF1A1033)
    val Night = Color(0xFF1B1538)

    val PlayGradient = listOf(Sakura, Violet)
    val ActionGradient = listOf(Cyan, Violet)
    val UltraGradient = listOf(Orange, Sakura)
    val SoftGradient = listOf(Color(0xFF4A3B8C), Color(0xFF30266A))

    /** 描边文字阴影：硬边偏移，漫画感 */
    val TextShadow = Shadow(color = Outline, offset = Offset(3f, 3f), blurRadius = 0f)
}

/**
 * 动漫风格文字：白字 + 硬边描影
 */
@Composable
fun AnimeLabel(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium
) {
    Text(
        modifier = modifier,
        text = text,
        maxLines = 1,
        color = Color.White,
        fontWeight = FontWeight.ExtraBold,
        style = style.copy(shadow = KadeAnime.TextShadow)
    )
}

/**
 * 动漫风格按钮：渐变填充、粗描边、硬边投影、高光，按下时下沉
 */
@Composable
fun AnimeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gradient: List<Color> = KadeAnime.PlayGradient,
    height: Dp = 52.dp,
    sparkle: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(percent = 50)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val drop by animateDpAsState(
        targetValue = if (pressed) 4.dp else 0.dp,
        label = "AnimeButtonDrop"
    )

    Box(
        modifier = modifier
            .height(height + 5.dp)
            .alpha(if (enabled) 1f else 0.45f)
    ) {
        //硬边投影
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(height)
                .clip(shape)
                .background(KadeAnime.Outline)
        )
        //按钮主体
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(height)
                .offset(y = drop)
                .clip(shape)
                .background(Brush.horizontalGradient(gradient))
                .border(3.dp, KadeAnime.Outline, shape)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            //高光
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.88f)
                    .height(height * 0.38f)
                    .padding(top = 5.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Color.White.copy(alpha = 0.26f))
            )
            Row(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = content
            )
            if (sparkle) {
                Text(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 20.dp, top = 6.dp),
                    text = "✦",
                    color = KadeAnime.Gold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * 动漫风格背景：柔和的樱花粉 / 青色光晕、速度线、四角闪光
 * 全部为静态绘制（只在尺寸变化时重绘），不影响性能
 */
@Composable
fun AnimeBackdrop(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.drawWithCache {
            val w = size.width
            val h = size.height
            val glowPink = Brush.radialGradient(
                colors = listOf(KadeAnime.Sakura.copy(alpha = 0.16f), Color.Transparent),
                center = Offset(w * 0.85f, h * 0.1f),
                radius = w * 0.6f
            )
            val glowCyan = Brush.radialGradient(
                colors = listOf(KadeAnime.Cyan.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(w * 0.1f, h * 0.95f),
                radius = w * 0.5f
            )
            onDrawBehind {
                drawRect(glowPink)
                drawRect(glowCyan)

                val line = Color.White.copy(alpha = 0.05f)
                for (i in 0 until 14) {
                    val x = w * (i / 13f)
                    drawLine(
                        color = line,
                        start = Offset(x, 0f),
                        end = Offset(x - h * 0.35f, h),
                        strokeWidth = if (i % 3 == 0) 5f else 2f
                    )
                }

                drawSparkle(Offset(w * 0.08f, h * 0.18f), 16f, KadeAnime.Gold.copy(alpha = 0.55f))
                drawSparkle(Offset(w * 0.46f, h * 0.9f), 12f, KadeAnime.Cyan.copy(alpha = 0.5f))
                drawSparkle(Offset(w * 0.93f, h * 0.62f), 18f, KadeAnime.Sakura.copy(alpha = 0.5f))
                drawSparkle(Offset(w * 0.3f, h * 0.06f), 10f, Color.White.copy(alpha = 0.4f))
                drawSparkle(Offset(w * 0.72f, h * 0.35f), 9f, KadeAnime.Gold.copy(alpha = 0.4f))
            }
        }
    )
}

/**
 * 四角星（用直线拼出，兼容所有 Compose 版本）
 */
private fun DrawScope.drawSparkle(center: Offset, r: Float, color: Color) {
    val k = r * 0.28f
    val path = Path().apply {
        moveTo(center.x, center.y - r)
        lineTo(center.x + k, center.y - k)
        lineTo(center.x + r, center.y)
        lineTo(center.x + k, center.y + k)
        lineTo(center.x, center.y + r)
        lineTo(center.x - k, center.y + k)
        lineTo(center.x - r, center.y)
        lineTo(center.x - k, center.y - k)
        close()
    }
    drawPath(path, color)
}
