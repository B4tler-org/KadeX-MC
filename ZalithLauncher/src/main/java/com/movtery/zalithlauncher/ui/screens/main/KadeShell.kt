/*
 * Zalith Launcher 2
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

package com.movtery.zalithlauncher.ui.screens.main

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.provider.Settings
import android.text.format.DateUtils
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.movtery.cardgrid.state.rememberCardGridState
import com.movtery.guide.GuideSide
import com.movtery.guide.guideNode
import com.movtery.zalithlauncher.BuildConfig
import com.movtery.zalithlauncher.BuildKeys
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.coroutine.Task
import com.movtery.zalithlauncher.coroutine.TaskSystem
import com.movtery.zalithlauncher.game.account.Account
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.account.getAccountTypeName
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.path.PathManager
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.setting.enums.ActionMenuSide
import com.movtery.zalithlauncher.ui.AndroidStringText
import com.movtery.zalithlauncher.ui.androidText
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.components.CardTitleLayout
import com.movtery.zalithlauncher.ui.components.MarqueeText
import com.movtery.zalithlauncher.ui.components.SkinPreview3D
import com.movtery.zalithlauncher.ui.components.TextRailItem
import com.movtery.zalithlauncher.ui.guide.GuideKeys
import com.movtery.zalithlauncher.ui.guide.sendStartGuideOnce
import com.movtery.zalithlauncher.ui.screens.BackStackNavKey
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.AccountManageScreen
import com.movtery.zalithlauncher.ui.screens.content.DownloadScreen
import com.movtery.zalithlauncher.ui.screens.content.FileSelectorScreen
import com.movtery.zalithlauncher.ui.screens.content.LauncherScreen
import com.movtery.zalithlauncher.ui.screens.content.LicenseScreen
import com.movtery.zalithlauncher.ui.screens.content.LogViewScreen
import com.movtery.zalithlauncher.ui.screens.content.MultiplayerScreen
import com.movtery.zalithlauncher.ui.screens.content.SettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionExportScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionSettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionsManageScreen
import com.movtery.zalithlauncher.ui.screens.content.WebViewScreen
import com.movtery.zalithlauncher.ui.screens.content.assetinfo.AssetInfoScreen
import com.movtery.zalithlauncher.ui.screens.content.elements.AnimeBackdrop
import com.movtery.zalithlauncher.ui.screens.content.elements.AnimeButton
import com.movtery.zalithlauncher.ui.screens.content.elements.AnimeLabel
import com.movtery.zalithlauncher.ui.screens.content.elements.CommonVersionInfoLayout
import com.movtery.zalithlauncher.ui.screens.content.elements.KadeAnime
import com.movtery.zalithlauncher.ui.screens.content.elements.KadeMusic
import com.movtery.zalithlauncher.ui.screens.content.elements.KadePerformanceSheet
import com.movtery.zalithlauncher.ui.screens.content.elements.KadeVideoBackground
import com.movtery.zalithlauncher.ui.screens.content.elements.PlayerFace
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage
import com.movtery.zalithlauncher.ui.screens.content.home.HomeGrid
import com.movtery.zalithlauncher.ui.screens.content.home.LocalActionMenuDrag
import com.movtery.zalithlauncher.ui.screens.content.home.actionMenuDragAnchor
import com.movtery.zalithlauncher.ui.screens.content.home.actionMenuDragExclusion
import com.movtery.zalithlauncher.ui.screens.content.home.rememberActionMenuDragState
import com.movtery.zalithlauncher.ui.screens.content.home.server.LocalServerCardQuickPlay
import com.movtery.zalithlauncher.ui.screens.content.home.version.LocalHomeCardLauncher
import com.movtery.zalithlauncher.ui.screens.content.home.version.LocalHomeCardVersionSettings
import com.movtery.zalithlauncher.ui.screens.content.navigateToDownload
import com.movtery.zalithlauncher.ui.screens.navigateTo
import com.movtery.zalithlauncher.ui.screens.onBack
import com.movtery.zalithlauncher.ui.screens.rememberTransitionSpec
import com.movtery.zalithlauncher.ui.theme.backgroundColor
import com.movtery.zalithlauncher.ui.theme.cardColor
import com.movtery.zalithlauncher.ui.theme.festivals.FestivalTitleText
import com.movtery.zalithlauncher.ui.theme.onBackgroundColor
import com.movtery.zalithlauncher.ui.theme.onCardColor
import com.movtery.zalithlauncher.utils.animation.getAnimateTween
import com.movtery.zalithlauncher.utils.animation.swapAnimateDpAsState
import com.movtery.zalithlauncher.utils.festival.LocalFestivals
import com.movtery.zalithlauncher.utils.file.formatFileSize
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.LocalBackgroundViewModel
import com.movtery.zalithlauncher.viewmodel.ModifyVersionViewModel
import com.movtery.zalithlauncher.viewmodel.ModpackImportViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import com.movtery.zalithlauncher.viewmodel.sendKeepScreen
import com.movtery.zalithlauncher.viewmodel.sendToast
import kotlin.math.roundToInt


internal object KadeDash {
    val Bg = Color(0xFF080818)
    val BgTop = Color(0xFF0D0D2B)
    val Panel = Color(0xFF111234)
    val Border = Color(0xFF2E3170)
    val Accent = Color(0xFF6C5CFF)
    val AccentLight = Color(0xFF9B8CFF)
    val Blue = Color(0xFF4F7CFF)
    val Muted = Color(0xFF9EA2D0)
    val Gold = Color(0xFFFFC857)
}

private data class KadeSideItem(
    val label: String,
    val iconRes: Int,
    val selected: Boolean,
    val onClick: () -> Unit
)

/**
 * Kade'X MC：所有页面共用的左侧导航栏
 * 当前页面对应的项目会高亮；所有入口都连接到启动器已有的真实页面
 */
@Composable
fun KadeSidebar(
    mainScreenKey: TitledNavKey?,
    onHome: () -> Unit,
    onLibrary: () -> Unit,
    onDownloads: () -> Unit,
    onVersions: () -> Unit,
    onMultiplayer: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inHome = mainScreenKey == null || mainScreenKey is NormalNavKey.LauncherMain
    val items = listOf(
        KadeSideItem("Home", R.drawable.ic_home_filled, inHome) { if (!inHome) onHome() },
        KadeSideItem("Library", R.drawable.ic_package_2_outlined, mainScreenKey is NormalNavKey.VersionsManager, onLibrary),
        KadeSideItem("Downloads", R.drawable.ic_download_2_outlined, mainScreenKey is NestedNavKey.Download, onDownloads),
        KadeSideItem("Versions", R.drawable.ic_style_outlined, mainScreenKey is NestedNavKey.VersionSettings, onVersions),
        KadeSideItem("Mods", R.drawable.ic_extension_outlined, false, onDownloads),
        KadeSideItem(stringResource(R.string.terracotta), R.drawable.ic_group_filled, mainScreenKey is NormalNavKey.Multiplayer, onMultiplayer),
        KadeSideItem("Settings", R.drawable.ic_settings_filled, mainScreenKey is NestedNavKey.Settings, onSettings)
    )
    Row(modifier = modifier.fillMaxHeight()) {
        KadeSidebarContent(items = items, onCreditsClick = onSettings)
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(KadeDash.Border.copy(alpha = 0.5f))
        )
    }
}

/**
 * Kade'X MC：背景音乐控制（所有页面都会播放）
 * 只在应用位于前台、且设置开启时播放；应用进入后台或启动游戏时暂停
 */
@Composable
fun KadeMusicController() {
    val context = LocalContext.current
    val musicEnabled = AllSettings.kadeMusic.state
    val owner = remember(context) {
        var c: Context? = context
        var found: LifecycleOwner? = null
        while (c is ContextWrapper && found == null) {
            if (c is LifecycleOwner) found = c
            c = c.baseContext
        }
        found
    }
    DisposableEffect(owner, musicEnabled) {
        val resume = {
            if (musicEnabled) KadeMusic.start(context) else KadeMusic.pause()
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> resume()
                Lifecycle.Event.ON_STOP -> KadeMusic.pause()
                else -> {}
            }
        }
        owner?.lifecycle?.addObserver(observer)
        if (owner == null || owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            resume()
        }
        onDispose {
            owner?.lifecycle?.removeObserver(observer)
            KadeMusic.pause()
        }
    }
}

/**
 * Kade'X MC：内容区顶部的细栏（返回按钮 + 页面标题 + 音乐开关 + 任务 + 文件管理）
 * 页面之间的切换由左侧导航栏负责
 */
@Composable
fun <E: TitledNavKey> KadeContentBar(
    mainScreenKey: E?,
    inLauncherScreen: Boolean,
    taskRunning: Boolean,
    isTasksExpanded: Boolean,
    modifier: Modifier = Modifier,
    contentColor: Color,
    onScreenBack: () -> Unit,
    openFileManager: () -> Unit,
    changeExpandedState: () -> Unit,
) {
    val festivals = LocalFestivals.current
    val musicEnabled = AllSettings.kadeMusic.state

    CompositionLocalProvider(
        LocalContentColor provides contentColor
    ) {
        ConstraintLayout(modifier = modifier) {
            val (backCenter, title, endButtons) = createRefs()

            val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

            Row(
                modifier = Modifier
                    .constrainAs(backCenter) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(12.dp))

                AnimatedVisibility(
                    visible = !inLauncherScreen
                ) {
                    IconButton(
                        modifier = Modifier.fillMaxHeight(),
                        onClick = {
                            if (!inLauncherScreen) {
                                //不在主屏幕时才允许返回
                                backDispatcher?.onBackPressed() ?: run {
                                    onScreenBack()
                                }
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.generic_back)
                        )
                    }
                }
            }
            val parentRes = mainScreenKey?.title
            val childRes = (mainScreenKey as? BackStackNavKey<*>)?.currentKey?.title

            Crossfade(
                modifier = Modifier.constrainAs(title) {
                    centerVerticallyTo(parent)
                    start.linkTo(backCenter.end, margin = 16.dp)
                },
                targetState = parentRes to childRes
            ) { (parent, child) ->
                val style = MaterialTheme.typography.titleMedium
                val softWarp = false
                val maxLines = 1

                if (parent == null) {
                    if (festivals.isEmpty()) {
                        androidx.compose.foundation.layout.Column {
                            Text(
                                text = BuildKeys.LAUNCHER_IDENTIFIER,
                                style = style,
                                softWrap = softWarp,
                                maxLines = maxLines
                            )
                            Text(
                                text = stringResource(R.string.unofficial_modified_label),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                softWrap = softWarp,
                                maxLines = maxLines
                            )
                        }
                    } else {
                        FestivalTitleText(
                            festivals = festivals,
                            style = style,
                            maxLines = maxLines
                        )
                    }
                } else {
                    val titleText = if (child != null) {
                        androidText(parent, androidText(" - "), child)
                    } else {
                        parent
                    }

                    AndroidStringText(
                        text = titleText,
                        style = style,
                        softWrap = softWarp,
                        maxLines = maxLines
                    )
                }
            }

            Row(
                modifier = Modifier
                    .constrainAs(endButtons) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end, margin = 12.dp)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(
                    visible = !(isTasksExpanded || taskRunning),
                    enter = slideInVertically(
                        initialOffsetY = { -50 }
                    ) + fadeIn(),
                    exit = slideOutVertically(
                        targetOffsetY = { -50 }
                    ) + fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .clip(shape = MaterialTheme.shapes.large)
                            .clickable { changeExpandedState() }
                            .padding(all = 8.dp)
                            .width(120.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(modifier = Modifier.weight(1f))
                        Icon(
                            modifier = Modifier.size(22.dp),
                            painter = painterResource(R.drawable.ic_assignment_filled),
                            contentDescription = stringResource(R.string.main_task_menu)
                        )
                    }
                }

                Text(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(KadeDash.Panel)
                        .border(1.dp, KadeDash.Border, CircleShape)
                        .clickable { AllSettings.kadeMusic.save(!musicEnabled) }
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    text = if (musicEnabled) "♪ On" else "♪ Off",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )

                IconButton(
                    onClick = openFileManager
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_folder_filled),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun KadeSidebarContent(
    items: List<KadeSideItem>,
    onCreditsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(212.dp)
            .fillMaxHeight()
            .background(KadeDash.Panel.copy(alpha = 0.55f))
            .verticalScroll(rememberScrollState())
            .padding(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp)),
                painter = painterResource(R.drawable.img_launcher),
                contentDescription = null
            )
            Column {
                Text(
                    text = BuildKeys.LAUNCHER_NAME,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "Play Beyond Limits",
                    color = KadeDash.Muted,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(
                        if (item.selected) KadeDash.Accent.copy(alpha = 0.30f) else Color.Transparent
                    )
                    .clickable(role = Role.Button, onClick = item.onClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(24.dp)
                        .background(if (item.selected) KadeDash.AccentLight else Color.Transparent)
                )
                Spacer(Modifier.width(13.dp))
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(item.iconRes),
                    contentDescription = null,
                    tint = if (item.selected) Color.White else KadeDash.Muted
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = item.label,
                    color = if (item.selected) Color.White else KadeDash.Muted,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, KadeDash.Border, RoundedCornerShape(16.dp))
                .clickable(onClick = onCreditsClick)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Inspired by",
                color = KadeDash.Muted,
                style = MaterialTheme.typography.labelSmall
            )
            listOf(
                R.drawable.ic_info_outlined to "Zalith Launcher",
                R.drawable.ic_github to "Github",
                R.drawable.ic_terminal_outlined to "Codespaces",
                R.drawable.ic_lightbulb to "AI Tools"
            ).forEach { (icon, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = KadeDash.AccentLight
                    )
                    Text(
                        text = label,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(KadeDash.Border.copy(alpha = 0.6f))
            )
            Text(
                text = "Owned by",
                color = KadeDash.Muted,
                style = MaterialTheme.typography.labelSmall
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "♛", color = KadeDash.Gold)
                Text(
                    text = "StrykeX",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            text = "v${BuildConfig.VERSION_NAME}  |  ${BuildKeys.LAUNCHER_NAME}",
            color = KadeDash.Muted,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

