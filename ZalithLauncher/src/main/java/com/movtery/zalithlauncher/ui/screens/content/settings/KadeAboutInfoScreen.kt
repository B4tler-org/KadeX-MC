/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * Kade'X MC is an unofficial modified version of Zalith Launcher 2.
 * Kade'X MC modifications are maintained by StrykeX.
 * The upstream copyright and the GPL-3.0 license above remain in force.
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

package com.movtery.zalithlauncher.ui.screens.content.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.movtery.zalithlauncher.BuildConfig
import com.movtery.zalithlauncher.BuildKeys
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.plugin.ApkPlugin
import com.movtery.zalithlauncher.game.plugin.PluginLoader
import com.movtery.zalithlauncher.game.plugin.appCacheIcon
import com.movtery.zalithlauncher.library.LibraryInfo
import com.movtery.zalithlauncher.library.libraryData
import com.movtery.zalithlauncher.path.URL_COMMUNITY
import com.movtery.zalithlauncher.path.URL_MCMOD
import com.movtery.zalithlauncher.path.URL_PROJECT
import com.movtery.zalithlauncher.path.URL_WEBLATE
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.AnimatedLazyColumn
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey

private const val URL_KADEX_PROJECT = "https://github.com/B4tler-org/KadeX-MC"
private const val URL_GITHUB = "https://github.com/"
private const val URL_CODESPACES = "https://github.com/features/codespaces"

/**
 * Kade'X MC：Liquid Glass 风格的关于页面
 * 与原有 AboutInfoScreen 参数一致，便于一行替换；
 * 不含任何赞助 / 捐赠入口。
 * 注意：原版的“检查更新”指向上游项目的更新源，因此这里刻意不提供该按钮。
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun KadeAboutInfoScreen(
    key: NestedNavKey.Settings,
    settingsScreenKey: TitledNavKey?,
    mainScreenKey: TitledNavKey?,
    checkUpdate: () -> Unit,
    openLicense: (raw: Int) -> Unit,
    openLink: (url: String) -> Unit
) {
    BaseScreen(
        Triple(key, mainScreenKey, false),
        Triple(NormalNavKey.Settings.AboutInfo, settingsScreenKey, false)
    ) { isVisible ->
        AnimatedLazyColumn(
            modifier = Modifier.fillMaxSize(),
            isVisible = isVisible,
            contentPadding = PaddingValues(all = 12.dp)
        ) { scope ->
            //应用身份
            animatedItem(scope) { yOffset ->
                GlassCard(modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) }) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            painter = painterResource(R.drawable.img_launcher),
                            contentDescription = null,
                            contentScale = ContentScale.Fit
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = BuildKeys.LAUNCHER_NAME,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                                    .padding(horizontal = 10.dp, vertical = 2.dp),
                                text = stringResource(R.string.about_launcher_version, BuildConfig.VERSION_NAME),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        modifier = Modifier.padding(top = 14.dp),
                        text = "A community-oriented Android launcher for Minecraft Java Edition.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = stringResource(R.string.unofficial_modified_notice, BuildKeys.LAUNCHER_NAME),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        modifier = Modifier.padding(top = 14.dp),
                        onClick = { openLink(URL_KADEX_PROJECT) }
                    ) {
                        Text(text = stringResource(R.string.about_launcher_project_link))
                    }
                }
            }

            //项目创建者
            animatedItem(scope) { yOffset ->
                GlassCard(modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) }) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "S",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Column {
                            Text(
                                text = "StrykeX",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Creator and maintainer of ${BuildKeys.LAUNCHER_NAME}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            //上游项目
            animatedItem(scope) { yOffset ->
                GlassCard(modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) }) {
                    GlassTitle("Upstream launcher")
                    GlassItem(
                        icon = R.drawable.ic_github,
                        useImage = false,
                        title = "Zalith Launcher 2",
                        text = "Upstream project and inspiration. Licensed under GPL-3.0.",
                        openLink = { openLink(URL_PROJECT) }
                    )
                    GlassItem(
                        icon = R.drawable.ic_group_filled,
                        useImage = false,
                        title = "Zalith Launcher 2 contributors",
                        text = "The original authors and contributors of the upstream launcher.",
                        openLink = { openLink(URL_COMMUNITY) }
                    )
                }
            }

            //上游致谢（保留原项目的致谢内容，仅移除赞助入口）
            animatedItem(scope) { yOffset ->
                GlassCard(modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) }) {
                    GlassTitle(stringResource(R.string.about_acknowledgements_title))
                    GlassItem(
                        icon = R.drawable.img_launcher_fcl,
                        title = "Fold Craft Launcher",
                        text = stringResource(R.string.about_acknowledgements_fcl_text, BuildKeys.LAUNCHER_SHORT_NAME),
                        openLicense = { openLicense(R.raw.fcl_license) },
                        openLink = { openLink("https://github.com/FCL-Team/FoldCraftLauncher") }
                    )
                    GlassItem(
                        icon = R.drawable.img_launcher_hmcl,
                        title = "Hello Minecraft! Launcher",
                        text = stringResource(R.string.about_acknowledgements_hmcl_text, BuildKeys.LAUNCHER_SHORT_NAME),
                        openLicense = { openLicense(R.raw.hmcl_license) },
                        openLink = { openLink("https://github.com/HMCL-dev/HMCL") }
                    )
                    GlassItem(
                        icon = R.drawable.img_launcher_pojav,
                        title = "PojavLauncher",
                        text = stringResource(R.string.about_acknowledgements_pojav_text, BuildKeys.LAUNCHER_SHORT_NAME),
                        openLicense = { openLicense(R.raw.lgpl_3_license) },
                        openLink = { openLink("https://github.com/PojavLauncherTeam/PojavLauncher") }
                    )
                    GlassItem(
                        icon = R.drawable.img_launcher_pcl2,
                        title = "Plain Craft Launcher 2",
                        text = stringResource(R.string.about_acknowledgements_pcl_text, BuildKeys.LAUNCHER_SHORT_NAME),
                        openLink = { openLink("https://github.com/Meloong-Git/PCL") }
                    )
                    GlassItem(
                        icon = R.drawable.img_platform_mcmod,
                        title = stringResource(R.string.about_acknowledgements_mcmod),
                        text = stringResource(R.string.about_acknowledgements_mcmod_text, BuildKeys.LAUNCHER_SHORT_NAME),
                        openLink = { openLink(URL_MCMOD) }
                    )
                    GlassItem(
                        icon = R.drawable.img_avatar_bangbang93,
                        title = "bangbang93",
                        text = stringResource(R.string.about_acknowledgements_bangbang93_text, BuildKeys.LAUNCHER_SHORT_NAME)
                    )
                    GlassItem(
                        icon = R.drawable.img_avatar_mcim,
                        title = "mcmod-info-mirror",
                        text = stringResource(R.string.about_acknowledgements_mcim_text, BuildKeys.LAUNCHER_SHORT_NAME)
                    )
                    GlassItem(
                        icon = R.drawable.img_platform_weblate,
                        title = stringResource(R.string.about_acknowledgements_weblate_community),
                        text = stringResource(R.string.about_acknowledgements_weblate_community_text),
                        openLink = { openLink(URL_WEBLATE) }
                    )
                }
            }

            //开发基础设施
            animatedItem(scope) { yOffset ->
                GlassCard(modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) }) {
                    GlassTitle("Development infrastructure")
                    GlassItem(
                        icon = R.drawable.ic_github,
                        useImage = false,
                        title = "GitHub",
                        text = "Source hosting and version control.",
                        openLink = { openLink(URL_GITHUB) }
                    )
                    GlassItem(
                        icon = R.drawable.ic_extension_outlined,
                        useImage = false,
                        title = "GitHub Codespaces",
                        text = "Cloud development environment.",
                        openLink = { openLink(URL_CODESPACES) }
                    )
                }
            }

            //AI 辅助开发
            animatedItem(scope) { yOffset ->
                GlassCard(modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) }) {
                    GlassTitle("AI-assisted development")
                    GlassItem(
                        icon = R.drawable.ic_lightbulb,
                        useImage = false,
                        title = "Development assistance",
                        text = "AI-assisted development tools were used to support parts of the development workflow. " +
                            "Final project integration, testing, and maintenance remain the responsibility of the project maintainer."
                    )
                }
            }

            //额外依赖库板块
            animatedItem(scope) { yOffset ->
                GlassCard(modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) }) {
                    GlassTitle(stringResource(R.string.about_library_title))
                    libraryData.forEach { info ->
                        LibraryInfoItem(info = info, openLicense = openLicense, openLink = openLink)
                    }
                }
            }

            //已加载插件板块
            PluginLoader.allPlugins.takeIf { it.isNotEmpty() }?.let { allPlugins ->
                animatedItem(scope) { yOffset ->
                    GlassCard(modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) }) {
                        GlassTitle(stringResource(R.string.about_plugin_title))
                        allPlugins.forEach { apkPlugin ->
                            PluginInfoItem(apkPlugin = apkPlugin)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 半透明“玻璃”卡片：柔和的渐变填充 + 发光描边
 */
@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(28.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.12f),
                        Color.White.copy(alpha = 0.04f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        colorScheme.primary.copy(alpha = 0.55f),
                        Color.White.copy(alpha = 0.12f),
                        colorScheme.primary.copy(alpha = 0.25f)
                    )
                ),
                shape = shape
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}

@Composable
private fun GlassTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}

/**
 * 玻璃卡片中的条目：图标 + 标题 + 说明，可选许可证 / 链接按钮
 * 没有对应入口的按钮不会显示，避免出现无效的可点击区域
 */
@Composable
private fun GlassItem(
    icon: Int,
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    useImage: Boolean = true,
    openLicense: (() -> Unit)? = null,
    openLink: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconModifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
        if (useImage) {
            Image(
                modifier = iconModifier,
                painter = painterResource(icon),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        } else {
            Icon(
                modifier = iconModifier,
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                modifier = Modifier.alpha(0.75f),
                text = text,
                style = MaterialTheme.typography.bodySmall
            )
        }

        openLicense?.let {
            IconButton(onClick = it) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(R.drawable.ic_copyright_outlined),
                    contentDescription = "License"
                )
            }
        }
        openLink?.let {
            IconButton(onClick = it) {
                Icon(
                    painter = painterResource(R.drawable.ic_link),
                    contentDescription = stringResource(R.string.generic_open_link)
                )
            }
        }
    }
}

@Composable
private fun PluginInfoItem(
    apkPlugin: ApkPlugin,
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.06f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
        onClick = {}
    ) {
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .padding(all = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val iconFile = appCacheIcon(apkPlugin.packageName)
            if (iconFile.exists()) {
                val model = remember(context, iconFile) {
                    ImageRequest.Builder(context)
                        .data(iconFile)
                        .build()
                }
                AsyncImage(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(shape = RoundedCornerShape(8.dp)),
                    model = model,
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            } else {
                Image(
                    modifier = Modifier.size(34.dp),
                    painter = painterResource(R.drawable.ic_unknown_icon),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = apkPlugin.appName,
                    style = MaterialTheme.typography.titleSmall
                )
                Row(
                    modifier = Modifier.alpha(0.7f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = apkPlugin.packageName,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (apkPlugin.appVersion.isNotEmpty()) {
                        Text(
                            text = apkPlugin.appVersion,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryInfoItem(
    info: LibraryInfo,
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.06f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    openLicense: (Int) -> Unit,
    openLink: (url: String) -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = info.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Column(
                    modifier = Modifier.alpha(0.7f)
                ) {
                    info.copyrightInfo?.let { copyrightInfo ->
                        Text(
                            text = copyrightInfo,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        modifier = Modifier.clickable(
                            onClick = {
                                openLicense(info.license.raw)
                            }
                        ),
                        text = "Licensed under the ${info.license.name}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
            }
            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    openLink(info.webUrl)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_link),
                    contentDescription = null
                )
            }
        }
    }
}