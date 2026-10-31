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

package com.movtery.zalithlauncher.ui.screens.content

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.provider.Settings
import android.text.format.DateUtils
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
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
import com.movtery.cardgrid.state.rememberCardGridState
import com.movtery.guide.GuideSide
import com.movtery.guide.guideNode
import com.movtery.zalithlauncher.BuildConfig
import com.movtery.zalithlauncher.BuildKeys
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.account.Account
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.account.getAccountTypeName
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.setting.enums.ActionMenuSide
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.components.MarqueeText
import com.movtery.zalithlauncher.ui.components.SkinPreview3D
import com.movtery.zalithlauncher.ui.guide.GuideKeys
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
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
import com.movtery.zalithlauncher.utils.animation.swapAnimateDpAsState
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import kotlin.math.roundToInt


private object KadeDash {
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

private data class KadeNewsItem(
    val title: String,
    val text: String,
    val iconRes: Int,
    val onClick: (() -> Unit)? = null
)

private fun Context.findKadeLifecycleOwner(): LifecycleOwner? {
    var c: Context? = this
    while (c is ContextWrapper) {
        if (c is LifecycleOwner) return c
        c = c.baseContext
    }
    return null
}

/**
 * Kade'X MC：新的主页仪表盘（左侧导航 + 欢迎视频 + 启动卡片 + 快捷入口 + 版本库）
 * 参数与 KadeLauncherScreen 一致；所有按钮都连接到启动器已有的真实功能
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun KadeHomeScreen(
    backStackViewModel: ScreenBackStackViewModel,
    navigateToVersions: (Version) -> Unit,
    onLaunchGame: (Version?) -> Unit,
    onQuickPlayServer: (Version?, String) -> Unit,
    onOpenLink: (String) -> Unit,
    startGuideOnce: (GuideKeys.Keys) -> Unit,
) {
    val context = LocalContext.current

    BaseScreen(
        screenKey = NormalNavKey.LauncherMain,
        currentKey = backStackViewModel.mainScreen.currentKey
    ) { isVisible ->
        val versions by VersionsManager.versions.collectAsStateWithLifecycle()
        val currentVersion by VersionsManager.currentVersion.collectAsStateWithLifecycle()
        val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
        val musicEnabled = AllSettings.kadeMusic.state
        val videoEnabled = AllSettings.kadeWelcomeVideo.state
        var showPerformance by remember { mutableStateOf(false) }

        //背景音乐：只在主页可见且应用位于前台时播放
        val lifecycleOwner = remember(context) { context.findKadeLifecycleOwner() }
        DisposableEffect(lifecycleOwner, musicEnabled, isVisible) {
            val owner = lifecycleOwner
            val resume = {
                if (musicEnabled && isVisible) KadeMusic.start(context) else KadeMusic.pause()
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

        //启动游戏前暂停音乐，并记录最近启动
        val launch: (Version?) -> Unit = { version ->
            recordKadeLaunch(version ?: VersionsManager.currentVersion.value)
            KadeMusic.pause()
            onLaunchGame(version)
        }

        val toLibrary: () -> Unit = {
            backStackViewModel.mainScreen.removeAndNavigateTo(
                remove = NestedNavKey.VersionSettings::class,
                screenKey = NormalNavKey.VersionsManager
            )
        }
        val toSettings: () -> Unit = {
            backStackViewModel.mainScreen.removeAndNavigateTo(
                removes = backStackViewModel.clearBeforeNavKeys,
                screenKey = backStackViewModel.settingsScreen
            )
        }
        val toDownloads: () -> Unit = { backStackViewModel.navigateToDownload() }
        val toAccounts: () -> Unit = {
            backStackViewModel.mainScreen.navigateTo(
                screenKey = NormalNavKey.AccountManager(FirstLoginMenu.NONE)
            )
        }
        val toVersionSettings: () -> Unit = {
            VersionsManager.currentVersion.value?.takeIf { it.isValid() }?.let { navigateToVersions(it) }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(KadeDash.BgTop, KadeDash.Bg)))
        ) {
            DashSidebar(
                items = listOf(
                    KadeSideItem("Home", R.drawable.ic_home_filled, true) {},
                    KadeSideItem("Library", R.drawable.ic_package_2_outlined, false, toLibrary),
                    KadeSideItem("Downloads", R.drawable.ic_download_2_outlined, false, toDownloads),
                    KadeSideItem("Versions", R.drawable.ic_style_outlined, false, toVersionSettings),
                    KadeSideItem("Mods", R.drawable.ic_extension_outlined, false, toDownloads),
                    KadeSideItem("Settings", R.drawable.ic_settings_filled, false, toSettings)
                ),
                onCreditsClick = toSettings
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(KadeDash.Border.copy(alpha = 0.5f))
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //顶部：账号 + 设置
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(KadeDash.Panel)
                                .border(1.dp, KadeDash.Border, RoundedCornerShape(16.dp))
                                .clickable(onClick = toAccounts)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            account?.let { PlayerFace(account = it, avatarSize = 32.dp) }
                            Column {
                                Text(
                                    text = account?.username ?: stringResource(R.string.account_add_new_account),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1
                                )
                                Text(
                                    text = "Java Edition",
                                    color = KadeDash.Muted,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.ic_keyboard_arrow_down),
                                contentDescription = null,
                                tint = KadeDash.Muted
                            )
                        }
                        IconButton(onClick = toSettings) {
                            Icon(
                                painter = painterResource(R.drawable.ic_settings_filled),
                                contentDescription = stringResource(R.string.generic_setting),
                                tint = Color.White
                            )
                        }
                    }
                }

                //欢迎区域：视频背景
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clipToBounds()
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF1B1550), Color(0xFF3A2D8C)))
                            )
                    ) {
                        if (videoEnabled) {
                            KadeVideoBackground(
                                modifier = Modifier.matchParentSize(),
                                active = isVisible
                            )
                        }
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            KadeDash.Bg.copy(alpha = 0.92f),
                                            KadeDash.Bg.copy(alpha = 0.55f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(horizontal = 22.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "WELCOME BACK,",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.labelLarge,
                                letterSpacing = 3.sp
                            )
                            Text(
                                text = account?.username ?: "Player",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color.White, KadeDash.AccentLight, KadeDash.Accent)
                                    ),
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Black,
                                    fontStyle = FontStyle.Italic
                                )
                            )
                            Text(
                                text = "Your next adventure is just a click away.",
                                color = KadeDash.Muted,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(KadeDash.Panel.copy(alpha = 0.8f))
                                    .border(1.dp, KadeDash.Border, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    modifier = Modifier.size(18.dp),
                                    painter = painterResource(R.drawable.ic_java),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                                Text(
                                    text = "Java Edition",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                        //音乐开关
                        Text(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.45f))
                                .clickable { AllSettings.kadeMusic.save(!musicEnabled) }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            text = if (musicEnabled) "♪ Music on" else "♪ Music off",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                //启动卡片
                item {
                    DashPanel {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val v = currentVersion
                            if (v != null && v.isValid()) {
                                CommonVersionInfoLayout(
                                    modifier = Modifier.weight(1f),
                                    version = v,
                                    iconSize = 56.dp
                                )
                                var menu by remember { mutableStateOf(false) }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Row(
                                        modifier = Modifier
                                            .height(52.dp)
                                            .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
                                            .background(
                                                Brush.horizontalGradient(listOf(KadeDash.Accent, KadeDash.Blue))
                                            )
                                            .clickable(role = Role.Button) { launch(null) }
                                            .padding(horizontal = 26.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(24.dp),
                                            painter = painterResource(R.drawable.ic_play_arrow_filled),
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                        Text(
                                            text = stringResource(R.string.main_launch_game),
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Box {
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 2.dp)
                                                .height(52.dp)
                                                .width(44.dp)
                                                .clip(RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp))
                                                .background(KadeDash.Accent.copy(alpha = 0.35f))
                                                .clickable(role = Role.Button) { menu = true },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_keyboard_arrow_down),
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = menu,
                                            onDismissRequest = { menu = false }
                                        ) {
                                            versions.forEach { item ->
                                                DropdownMenuItem(
                                                    text = { Text(text = item.getVersionName()) },
                                                    onClick = {
                                                        VersionsManager.saveVersion(item)
                                                        menu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = stringResource(R.string.versions_manage_no_versions),
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            Brush.horizontalGradient(listOf(KadeDash.Accent, KadeDash.Blue))
                                        )
                                        .clickable(onClick = toDownloads)
                                        .padding(horizontal = 22.dp, vertical = 14.dp),
                                    text = stringResource(R.string.generic_download),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                //快捷入口
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashTile(
                            modifier = Modifier.weight(1f),
                            iconRes = R.drawable.ic_rocket_launch_filled,
                            title = "Performance",
                            subtitle = "FPS Boost & Optimizations",
                            onClick = { showPerformance = true }
                        )
                        DashTile(
                            modifier = Modifier.weight(1f),
                            iconRes = R.drawable.ic_extension_outlined,
                            title = "Mods",
                            subtitle = "Find and install mods",
                            onClick = toDownloads
                        )
                        DashTile(
                            modifier = Modifier.weight(1f),
                            iconRes = R.drawable.ic_lightbulb,
                            title = "Shaders",
                            subtitle = "Find and install shaders",
                            onClick = toDownloads
                        )
                        DashTile(
                            modifier = Modifier.weight(1f),
                            iconRes = R.drawable.ic_settings_filled,
                            title = "Settings",
                            subtitle = "Launcher & game settings",
                            onClick = toSettings
                        )
                    }
                }

                //更新说明（均为本应用的真实功能说明）
                item { DashSectionTitle("What's new") }
                val news = listOf(
                    KadeNewsItem(
                        title = "${BuildKeys.LAUNCHER_NAME} ${BuildConfig.VERSION_NAME}",
                        text = "New dashboard, anime style and the FPS Booster.",
                        iconRes = R.drawable.ic_star_filled
                    ),
                    KadeNewsItem(
                        title = "FPS Booster",
                        text = "Balanced, Performance and Ultra presets, with a one-tap restore.",
                        iconRes = R.drawable.ic_rocket_launch_filled,
                        onClick = { showPerformance = true }
                    ),
                    KadeNewsItem(
                        title = "Music and welcome video",
                        text = "Both can be switched off in Settings, Launcher, Animation.",
                        iconRes = R.drawable.ic_movie_filled
                    )
                )
                items(news) { DashNewsCard(it) }

                //版本库
                item { DashSectionTitle("Your Library") }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(versions) { v ->
                            DashLibraryTile(version = v, onPlay = { launch(v) })
                        }
                        item {
                            Column(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(112.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(KadeDash.Panel)
                                    .border(1.dp, KadeDash.Border, RoundedCornerShape(18.dp))
                                    .clickable(onClick = toDownloads),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "+",
                                    color = KadeDash.AccentLight,
                                    fontSize = 30.sp
                                )
                                Text(
                                    text = "Add New",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }

                //结尾语
                item {
                    DashPanel {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "“Good players build. Great ones create.”",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontStyle = FontStyle.Italic
                            )
                            Text(
                                text = "— ${BuildKeys.LAUNCHER_NAME}",
                                color = KadeDash.Muted,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }

        if (showPerformance) {
            KadePerformanceSheet(
                version = currentVersion,
                onDismiss = { showPerformance = false }
            )
        }
    }
}

@Composable
private fun DashSidebar(
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

@Composable
private fun DashPanel(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(KadeDash.Panel)
            .border(1.dp, KadeDash.Border.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
    ) {
        content()
    }
}

@Composable
private fun DashSectionTitle(text: String) {
    Text(
        text = text,
        color = Color.White,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun DashTile(
    iconRes: Int,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(KadeDash.Panel)
            .border(1.dp, KadeDash.Border.copy(alpha = 0.8f), RoundedCornerShape(18.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            modifier = Modifier.size(26.dp),
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = KadeDash.AccentLight
        )
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            color = KadeDash.Muted,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2
        )
    }
}

@Composable
private fun DashNewsCard(item: KadeNewsItem) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(KadeDash.Panel)
            .border(1.dp, KadeDash.Border.copy(alpha = 0.8f), shape)
            .then(
                if (item.onClick != null) Modifier.clickable(role = Role.Button, onClick = item.onClick)
                else Modifier
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(KadeDash.Accent, KadeDash.Blue))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(item.iconRes),
                contentDescription = null,
                tint = Color.White
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.text,
                color = KadeDash.Muted,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun DashLibraryTile(
    version: Version,
    onPlay: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = Modifier
            .width(150.dp)
            .height(112.dp)
            .clip(shape)
            .background(KadeDash.Panel)
            .border(1.dp, KadeDash.Border.copy(alpha = 0.8f), shape)
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VersionIconImage(
                version = version,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Text(
                modifier = Modifier.weight(1f),
                text = version.getVersionName(),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.End)
                .size(34.dp)
                .clip(CircleShape)
                .background(KadeDash.Accent.copy(alpha = 0.4f))
                .clickable(role = Role.Button, onClick = onPlay),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(22.dp),
                painter = painterResource(R.drawable.ic_play_arrow_filled),
                contentDescription = stringResource(R.string.main_launch_game),
                tint = Color.White
            )
        }
    }
}
