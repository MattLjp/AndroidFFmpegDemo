package com.matt.nativelib

import android.opengl.GLSurfaceView
import android.view.Surface
import android.view.SurfaceView
import com.matt.nativelib.media.decoder.AudioDecoder
import com.matt.nativelib.media.decoder.VideoDecoder
import com.matt.nativelib.media.render.*

/**
 * @ Author : 廖健鹏
 * @ Date : 2022/2/21
 * @ e-mail : 329524627@qq.com
 * @ Description :
 */
class Player {

    private var audioDecoder: AudioDecoder? = null
    private var videoDecoder: VideoDecoder? = null


    fun create(
        decoderType: DecoderType,
        url: String,
        surfaceView: SurfaceView? = null,
        surface: Surface? = null,
        glSurfaceView: GLSurfaceView? = null,
    ) {

        if (decoderType == DecoderType.NativeDecoder) {
            if (glSurfaceView == null && surfaceView == null && surface == null) return
            audioDecoder = AudioDecoder(url).apply {
                audioVideoRender = AudioRender()
            }
            videoDecoder = VideoDecoder(url)
            if (glSurfaceView != null) {
//                val videoRender = OpenGlRender()
//                val render = SimpleGLRender().apply {
//                    render = videoRender
//                    getSurfaceTexture = {
//                        videoDecoder = VideoDecoder(url, null, Surface(it))
//                        videoDecoder?.audioVideoRender = audioRender
//                        videoDecoder?.videoVideoRender = videoRender
//                    }
//                }
//                glSurfaceView.setEGLContextClientVersion(2)
//                glSurfaceView.setRenderer(render)
            } else {
                videoDecoder?.videoVideoRender = VideoRender(surfaceView, surface)
            }
            audioDecoder!!.initDecoder()
            videoDecoder!!.initDecoder()
        } else {

        }
    }

    fun play() {
        audioDecoder?.start()
        videoDecoder?.start()
    }

    fun pause() {
        audioDecoder?.pause()
        videoDecoder?.pause()
    }

    fun stop() {
        audioDecoder?.stop()
        videoDecoder?.stop()
    }

    fun release(){
        audioDecoder?.release()
        videoDecoder?.release()
    }

    enum class DecoderType {
        NativeDecoder,
        FFmpegDecoder
    }
}