/*
 * Kade'X MC - unofficial modified version of Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.ui.AndroidStringText
import com.movtery.zalithlauncher.ui.theme.cardColor
import com.movtery.zalithlauncher.ui.theme.onCardColor
import com.movtery.zalithlauncher.utils.file.formatFileSize
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel

/**
 * Kade'X MC：版本库卡片
 * 直接复用原有的 [VersionItemLayout]（所有功能与回调保持不变），
 * 只在外层增加：选中描边、选中着色、左侧强调条
 */
@Composable
fun KadeVersionItemLayout(
    version: Version,
    selected: Boolean,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
    modifier: Modifier = Modifier,
    onSelected: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onRenameClick: () -> Unit = {},
    onCopyClick: () -> Unit = {},
    onExportClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onPinned: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val shape = MaterialTheme.shapes.large

    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            colorScheme.primary
        } else {
            colorScheme.outlineVariant.copy(alpha = 0.35f)
        },
        label = "KadeVersionBorder"
    )
    val baseColor = cardColor()
    val cardTint = if (selected) lerp(baseColor, colorScheme.primary, 0.14f) else baseColor

    Box(
        modifier = modifier
            .clip(shape)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = shape
            )
    ) {
        VersionItemLayout(
            modifier = Modifier.fillMaxWidth(),
            version = version,
            selected = selected,
            submitError = submitError,
            color = cardTint,
            onSelected = onSelected,
            onSettingsClick = onSettingsClick,
            onRenameClick = onRenameClick,
            onCopyClick = onCopyClick,
            onExportClick = onExportClick,
            onDeleteClick = onDeleteClick,
            onPinned = onPinned
        )

        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                    .background(colorScheme.primary)
            )
        }
    }
}

/**
 * Kade'X MC：任务 / 下载进度卡片
 * 参数与原有的私有 TaskItem 完全一致，进度、速度、取消均来自真实任务状态
 */
@Composable
fun KadeTaskItem(
    taskProgress: Float,
    taskMessage: AndroidStringText?,
    rateBytesPerSec: Long?,
    modifier: Modifier = Modifier,
    color: Color = cardColor(false),
    contentColor: Color = onCardColor(),
    onCancelClick: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = color,
        contentColor = contentColor,
        border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                taskMessage?.let { message ->
                    AndroidStringText(
                        text = message,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                val barModifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)

                if (taskProgress < 0) { //负数则代表不确定
                    LinearProgressIndicator(
                        modifier = barModifier,
                        color = colorScheme.primary,
                        trackColor = colorScheme.surfaceContainerHighest,
                        strokeCap = StrokeCap.Round
                    )
                } else {
                    val animatedProgress by animateFloatAsState(
                        targetValue = taskProgress.coerceIn(0f, 1f),
                        label = "KadeTaskProgress"
                    )
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = barModifier,
                        color = colorScheme.primary,
                        trackColor = colorScheme.surfaceContainerHighest,
                        strokeCap = StrokeCap.Round
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    taskProgress.takeIf { it >= 0f }?.let { progress ->
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                    }
                    rateBytesPerSec?.let { bytes ->
                        val text = remember(bytes) { "${formatFileSize(bytes)}/s" }
                        Text(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(colorScheme.surfaceContainerHighest)
                                .padding(horizontal = 8.dp, vertical = 1.dp),
                            text = text,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colorScheme.surfaceContainerHighest),
                onClick = onCancelClick
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.generic_cancel)
                )
            }
        }
    }
}
