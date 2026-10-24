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

package com.movtery.zalithlauncher.ui.screens.content.elements

import android.app.ActivityManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.setting.AllSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 一条会写入 options.txt 的选项
 * 只会修改文件中已存在的键（保持原有的数据类型），除非 addIfMissing 为 true
 */
data class KadePerfOption(
    val key: String,
    val kind: Kind,
    val value: String,
    val addIfMissing: Boolean = false
) {
    enum class Kind { INT, FLOAT, BOOL_OFF }
}

private fun intOpt(key: String, value: Int, addIfMissing: Boolean = false) =
    KadePerfOption(key, KadePerfOption.Kind.INT, value.toString(), addIfMissing)

private fun floatOpt(key: String, value: String) =
    KadePerfOption(key, KadePerfOption.Kind.FLOAT, value)

private fun offOpt(key: String) =
    KadePerfOption(key, KadePerfOption.Kind.BOOL_OFF, "false")

/**
 * 性能预设：降低通常最影响帧率的游戏内选项，并调整启动器的两项全局设置
 */
enum class KadePerfPreset(
    val title: String,
    val subtitle: String,
    val gradient: List<Color>,
    val resolutionRatio: Int,
    val sustained: Boolean,
    val summary: String,
    val options: List<KadePerfOption>
) {
    BALANCED(
        title = "Balanced",
        subtitle = "Small boost, looks almost the same",
        gradient = KadeAnime.ActionGradient,
        resolutionRatio = 100,
        sustained = false,
        summary = "render distance 8, simulation distance 8, decreased particles",
        options = listOf(
            intOpt("renderDistance", 8, addIfMissing = true),
            intOpt("simulationDistance", 8),
            intOpt("particles", 1),
            intOpt("biomeBlendRadius", 2)
        )
    ),
    PERFORMANCE(
        title = "Performance",
        subtitle = "Noticeable boost for mid-range phones",
        gradient = KadeAnime.PlayGradient,
        resolutionRatio = 85,
        sustained = true,
        summary = "render distance 6, simulation distance 6, minimal particles, fast graphics, " +
            "smooth lighting off, entity shadows off",
        options = listOf(
            intOpt("renderDistance", 6, addIfMissing = true),
            intOpt("simulationDistance", 6),
            intOpt("particles", 2),
            intOpt("graphicsMode", 0),
            offOpt("fancyGraphics"),
            offOpt("ao"),
            offOpt("entityShadows"),
            intOpt("biomeBlendRadius", 1),
            floatOpt("entityDistanceScaling", "0.75")
        )
    ),
    ULTRA(
        title = "Ultra Optimization",
        subtitle = "Maximum FPS for weak or hot devices",
        gradient = KadeAnime.UltraGradient,
        resolutionRatio = 70,
        sustained = true,
        summary = "render distance 4, simulation distance 5, minimal particles, fast graphics, " +
            "smooth lighting off, entity shadows off, shorter entity distance",
        options = listOf(
            intOpt("renderDistance", 4, addIfMissing = true),
            intOpt("simulationDistance", 5),
            intOpt("particles", 2),
            intOpt("graphicsMode", 0),
            offOpt("fancyGraphics"),
            offOpt("ao"),
            offOpt("entityShadows"),
            intOpt("biomeBlendRadius", 0),
            floatOpt("entityDistanceScaling", "0.5")
        )
    );
}

object KadePerformance {
    private const val BACKUP_NAME = "options.txt.kade-backup"

    fun hasBackup(version: Version): Boolean =
        runCatching { File(version.getGameDir(), BACKUP_NAME).exists() }.getOrDefault(false)

    fun hasLauncherBackup(): Boolean = AllSettings.kadePerfPrevious.state.isNotEmpty()

    /**
     * 应用预设
     * 1. 首次应用时备份 options.txt，并记录启动器当前的分辨率比例与持续性能模式
     * 2. 只改动 options.txt 中存在的键，保持原有数据类型
     * @return 实际改动的选项数量
     */
    fun apply(version: Version, preset: KadePerfPreset): Int {
        val dir = version.getGameDir()
        dir.mkdirs()
        val file = File(dir, "options.txt")
        val backup = File(dir, BACKUP_NAME)
        if (file.exists() && !backup.exists()) file.copyTo(backup)

        val lines = if (file.exists()) file.readLines().toMutableList() else mutableListOf()
        var changed = 0
        preset.options.forEach { opt ->
            val idx = lines.indexOfFirst { it.startsWith(opt.key + ":") }
            if (idx >= 0) {
                val existing = lines[idx].substring(opt.key.length + 1)
                val newValue = convert(opt, existing)
                if (newValue != existing) {
                    lines[idx] = opt.key + ":" + newValue
                    changed++
                }
            } else if (opt.addIfMissing) {
                lines.add(opt.key + ":" + convert(opt, ""))
                changed++
            }
        }

        val tmp = File(dir, "options.txt.kade-tmp")
        tmp.writeText(lines.joinToString("\n") + "\n")
        if (!tmp.renameTo(file)) {
            file.writeText(tmp.readText())
            tmp.delete()
        }

        if (AllSettings.kadePerfPrevious.state.isEmpty()) {
            AllSettings.kadePerfPrevious.save(
                "${AllSettings.resolutionRatio.state}|${AllSettings.sustainedPerformance.state}"
            )
        }
        AllSettings.resolutionRatio.save(preset.resolutionRatio)
        AllSettings.sustainedPerformance.save(preset.sustained)
        return changed
    }

    /**
     * 恢复：还原 options.txt 备份与启动器的两项全局设置
     */
    fun restore(version: Version): Boolean {
        var restored = false
        val dir = version.getGameDir()
        val backup = File(dir, BACKUP_NAME)
        if (backup.exists()) {
            backup.copyTo(File(dir, "options.txt"), overwrite = true)
            backup.delete()
            restored = true
        }
        val previous = AllSettings.kadePerfPrevious.state
        if (previous.isNotEmpty()) {
            val parts = previous.split('|')
            parts.getOrNull(0)?.toIntOrNull()?.let { AllSettings.resolutionRatio.save(it) }
            parts.getOrNull(1)?.toBooleanStrictOrNull()?.let { AllSettings.sustainedPerformance.save(it) }
            AllSettings.kadePerfPrevious.save("")
            restored = true
        }
        return restored
    }

    private fun convert(opt: KadePerfOption, existing: String): String = when (opt.kind) {
        KadePerfOption.Kind.INT, KadePerfOption.Kind.FLOAT -> opt.value
        KadePerfOption.Kind.BOOL_OFF -> when {
            existing.trim('"').toIntOrNull() != null -> "0"
            existing.startsWith("\"") -> "\"false\""
            else -> "false"
        }
    }

    fun deviceMemoryText(context: Context): String {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: return "unknown"
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        return String.format("%.1f GB", info.totalMem / 1024.0 / 1024.0 / 1024.0)
    }
}

/**
 * Kade'X MC：FPS Booster 面板
 * 如实说明：只调整真实存在的设置，效果因设备和版本而异，不保证具体的帧率提升
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KadePerformanceSheet(
    version: Version?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val usable = version != null && version.isValid()

    var pending by remember { mutableStateOf<KadePerfPreset?>(null) }
    var status by remember { mutableStateOf<String?>(null) }
    var canRestore by remember {
        mutableStateOf((version != null && KadePerformance.hasBackup(version)) || KadePerformance.hasLauncherBackup())
    }
    val memoryText = remember { KadePerformance.deviceMemoryText(context) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = KadeAnime.Night,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimeLabel(text = "FPS Booster ✦", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Device memory: $memoryText",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = "Lowers in-game and launcher settings that commonly improve FPS on weak devices. " +
                    "Results vary by device and version, and no specific FPS gain is guaranteed. " +
                    "A backup is kept so you can restore everything.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f)
            )
            if (!usable) {
                Text(
                    text = "Select a valid installation first.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KadeAnime.Gold
                )
            }

            KadePerfPreset.entries.forEach { preset ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    AnimeButton(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = usable,
                        gradient = preset.gradient,
                        height = 56.dp,
                        onClick = { pending = preset }
                    ) {
                        AnimeLabel(text = preset.title)
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        text = preset.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            if (canRestore && version != null) {
                AnimeButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    gradient = KadeAnime.SoftGradient,
                    height = 48.dp,
                    sparkle = false,
                    onClick = {
                        scope.launch {
                            val ok = withContext(Dispatchers.IO) {
                                runCatching { KadePerformance.restore(version) }.getOrDefault(false)
                            }
                            canRestore = false
                            status = if (ok) "Previous settings restored." else "Nothing to restore."
                        }
                    }
                ) {
                    AnimeLabel(text = "Restore previous settings")
                }
            }

            status?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = KadeAnime.Gold
                )
            }
        }
    }

    val chosen = pending
    if (chosen != null && version != null) {
        AlertDialog(
            onDismissRequest = { pending = null },
            title = { Text(text = chosen.title) },
            text = {
                Text(
                    text = "Launcher (all versions):\n" +
                        "• Resolution scale: ${chosen.resolutionRatio}%\n" +
                        "• Sustained performance: ${if (chosen.sustained) "on" else "off"}\n\n" +
                        "In-game options for ${version.getVersionName()}:\n" +
                        "• ${chosen.summary}\n\n" +
                        "Only options that exist in options.txt are changed, and a backup is saved. " +
                        "The effect shows the next time you launch the game."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    pending = null
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            runCatching { KadePerformance.apply(version, chosen) }
                        }
                        result.onSuccess {
                            canRestore = true
                            status = "${chosen.title} applied ($it in-game options changed). Launch the game to see the effect."
                        }.onFailure {
                            status = "Could not apply: ${it.message ?: "unknown error"}"
                        }
                    }
                }) {
                    Text(text = "Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { pending = null }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}
