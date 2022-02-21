package com.matt.nativelib

import android.opengl.GLSurfaceView
import android.view.Surface
import android.view.SurfaceView
import com.matt.nativelib.media.decoder.AudioDecoder
import com.matt.nativelib.media.decoder.VideoDecoder

/**
 * @ Author : 廖健鹏
 * @ Date : 2022/2/21
 * @ e-mail : 329524627@qq.com
 * @ Description :
 */
object PlayerManager {

    private var audioDecoder: AudioDecoder? = null
    private var videoDecoder: VideoDecoder? = null


    fun create(
        decoderType: DecoderType,
        url: String,
        glSurfaceView: GLSurfaceView?,
        surfaceView: SurfaceView?,
        surface: Surface?
    ) {
        if (decoderType == DecoderType.MediaDecoder) {
            if (glSurfaceView == null && surfaceView == null && surface == null) return
            audioDecoder = AudioDecoder(url)
            if (glSurfaceView != null) {


            } else {
                videoDecoder = VideoDecoder(url, surfaceView, surface)
            }

        } else {

        }

    }

    enum class DecoderType {
        MediaDecoder,
        FFmpegDecoder
    }
}