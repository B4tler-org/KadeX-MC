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

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Kade'X MC：主页背景音乐
 * 使用 res/raw/kade_bgm.mp3，循环播放；离开主页、应用进入后台或启动游戏时会暂停 / 停止
 */
object KadeMusic {
    private var player: MediaPlayer? = null

    /** 供界面观察的播放状态 */
    var isPlaying by mutableStateOf(false)
        private set

    fun start(context: Context) {
        runCatching {
            if (player == null) {
                val id = context.resources.getIdentifier("kade_bgm", "raw", context.packageName)
                if (id == 0) return //没有放入音乐文件：保持静音
                player = MediaPlayer.create(context.applicationContext, id)?.apply {
                    isLooping = true
                    setVolume(0.35f, 0.35f)
                }
            }
            player?.let { if (!it.isPlaying) it.start() }
            isPlaying = player?.isPlaying == true
        }
    }

    fun pause() {
        runCatching { player?.takeIf { it.isPlaying }?.pause() }
        isPlaying = false
    }

    /** 完全释放（启动游戏前调用，避免游戏时仍在播放） */
    fun stop() {
        runCatching { player?.release() }
        player = null
        isPlaying = false
    }
}

/**
 * Kade'X MC：循环静音视频背景（res/raw/kade_welcome.mp4）
 * 使用 TextureView + MediaPlayer，画面以“居中裁切”方式铺满区域
 * [active] 为 false 时暂停，减少耗电
 */
@Composable
fun KadeVideoBackground(
    modifier: Modifier = Modifier,
    active: Boolean = true
) {
    val holder = remember { KadeVideoHolder() }
    DisposableEffect(Unit) {
        onDispose { holder.release() }
    }
    LaunchedEffect(active) {
        holder.setActive(active)
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextureView(ctx).apply {
                val view = this
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        holder.attach(ctx, view, surface)
                    }

                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                        holder.applyCrop(view)
                    }

                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                        holder.release()
                        return true
                    }

                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                }
            }
        }
    )
}

private class KadeVideoHolder {
    private var mediaPlayer: MediaPlayer? = null
    private var surface: Surface? = null
    private var prepared = false
    private var wantPlay = true

    fun attach(context: Context, view: TextureView, texture: SurfaceTexture) {
        release()
        val id = context.resources.getIdentifier("kade_welcome", "raw", context.packageName)
        if (id == 0) return //没有放入视频文件：只显示渐变背景
        val player = MediaPlayer()
        try {
            val afd = context.resources.openRawResourceFd(id)
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            val s = Surface(texture)
            surface = s
            player.setSurface(s)
            player.isLooping = true
            player.setVolume(0f, 0f)
            player.setOnPreparedListener {
                prepared = true
                applyCrop(view)
                if (wantPlay) it.start()
            }
            player.setOnErrorListener { _, _, _ -> true }
            player.prepareAsync()
            mediaPlayer = player
        } catch (e: Exception) {
            runCatching { player.release() }
        }
    }

    fun setActive(active: Boolean) {
        wantPlay = active
        val p = mediaPlayer ?: return
        if (!prepared) return
        runCatching { if (active) p.start() else p.pause() }
    }

    /** TextureView 会把视频拉伸到整个视图，这里按比例放大并交给父容器裁切，实现“居中裁切” */
    fun applyCrop(view: TextureView) {
        val p = mediaPlayer ?: return
        val vw = p.videoWidth
        val vh = p.videoHeight
        if (vw <= 0 || vh <= 0 || view.width == 0 || view.height == 0) return
        val viewAspect = view.width.toFloat() / view.height
        val videoAspect = vw.toFloat() / vh
        if (videoAspect > viewAspect) {
            view.scaleX = videoAspect / viewAspect
            view.scaleY = 1f
        } else {
            view.scaleX = 1f
            view.scaleY = viewAspect / videoAspect
        }
    }

    fun release() {
        prepared = false
        runCatching { mediaPlayer?.release() }
        mediaPlayer = null
        runCatching { surface?.release() }
        surface = null
    }
}
