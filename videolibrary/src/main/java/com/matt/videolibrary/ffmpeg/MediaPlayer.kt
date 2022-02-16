package com.matt.videolibrary.ffmpeg

import android.view.Surface

/**
 * @ Author : 廖健鹏
 * @ Date : 2022/2/14
 * @ e-mail : 329524627@qq.com
 * @ Description :
 */
object MediaPlayer {
    init {
        System.loadLibrary("android-ffmpeg")
    }

    external fun ffmpegInfo(): String

    external fun initEncoder(srcPath: String, destPath: String): Int

    external fun startEncoder(encoder: Int)

    external fun releaseEncoder(encoder: Int)

    external fun createPlayer(path: String, surface: Surface): Int

    external fun play(player: Int)

    external fun pause(player: Int)

    external fun createGLPlayer(path: String, surface: Surface): Int

    external fun playOrPause(player: Int)

    external fun stop(player: Int)

    external fun createRepack(srcPath: String, destPath: String): Int

    external fun startRepack(repack: Int)
}