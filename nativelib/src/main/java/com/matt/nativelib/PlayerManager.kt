package com.matt.nativelib

import android.opengl.GLSurfaceView
import android.view.Surface
import android.view.SurfaceView
import com.matt.nativelib.media.decoder.AudioDecoder
import com.matt.nativelib.media.decoder.VideoDecoder
import com.matt.nativelib.media.render.AudioRender
import com.matt.nativelib.media.render.IAudioRender
import com.matt.nativelib.media.render.IVideoRender
import com.matt.nativelib.media.render.OpenGlRender
import com.matt.nativelib.media.render.SimpleGLRender

/**
 * @ Author : 廖健鹏
 * @ Date : 2022/2/21
 * @ e-mail : 329524627@qq.com
 * @ Description :
 */
object PlayerManager {

    private var audioDecoder: AudioDecoder? = null
    private var videoDecoder: VideoDecoder? = null
    private var audioRender: IAudioRender? = null
    private var videoRender: IVideoRender? = null


    fun create(
        decoderType: DecoderType,
        url: String,
        glSurfaceView: GLSurfaceView?,
        surfaceView: SurfaceView?,
        surface: Surface?
    ) {
        audioRender = AudioRender()
        videoRender = OpenGlRender()

        if (decoderType == DecoderType.MediaDecoder) {
            if (glSurfaceView == null && surfaceView == null && surface == null) return
            audioDecoder = AudioDecoder(url)
            if (glSurfaceView != null) {
                val videoRender = OpenGlRender()
                val render = SimpleGLRender().apply {
                    render = videoRender
                    getSurfaceTexture = {
                        videoDecoder = VideoDecoder(url, null, Surface(it))
                        videoDecoder?.audioVideoRender = audioRender
                        videoDecoder?.videoVideoRender = videoRender
                    }
                }
                glSurfaceView.setEGLContextClientVersion(2)
                glSurfaceView.setRenderer(render)
            } else {
                videoDecoder = VideoDecoder(url, surfaceView, surface)
                videoDecoder?.audioVideoRender = audioRender
                videoDecoder?.videoVideoRender = videoRender
            }

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

    enum class DecoderType {
        MediaDecoder,
        FFmpegDecoder
    }
}