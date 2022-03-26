package com.matt.nativelib

import android.opengl.GLSurfaceView
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import com.matt.nativelib.media.Frame
import com.matt.nativelib.media.decoder.IDecoderStateListener
import com.matt.nativelib.media.decoder.AudioDecoder
import com.matt.nativelib.media.decoder.BaseDecoder
import com.matt.nativelib.media.decoder.VideoDecoder
import java.util.concurrent.Executors

/**
 * @ Author : 廖健鹏
 * @ Date : 2022/2/21
 * @ e-mail : 329524627@qq.com
 * @ Description :
 */
class Player {

    private val TAG = javaClass.simpleName
    private var audioDecoder: AudioDecoder? = null
    private var videoDecoder: VideoDecoder? = null

    private val listener = object : IDecoderStateListener {
        override fun decoderReady(decodeJob: BaseDecoder?) {

        }

        override fun decoderRunning(decodeJob: BaseDecoder?) {

        }

        override fun decoderPause(decodeJob: BaseDecoder?) {

        }

        override fun decodeOneFrame(
            decodeJob: BaseDecoder?,
            frame: Frame,
            time: Long,
            pos: Long
        ) {
            Log.d(TAG, "进度: $pos")
        }

        override fun decoderFinish(decodeJob: BaseDecoder?) {

        }

        override fun decoderDestroy(decodeJob: BaseDecoder?) {

        }

        override fun decoderError(decodeJob: BaseDecoder?, msg: String) {

        }

    }

    fun init(
        decoderType: DecoderType,
        url: String,
        surfaceView: SurfaceView? = null,
        surface: Surface? = null,
        glSurfaceView: GLSurfaceView? = null,
    ) {
        val threadPool = Executors.newFixedThreadPool(10)
        audioDecoder = AudioDecoder(url)

        if (decoderType == DecoderType.NativeDecoder) {
            if (glSurfaceView == null && surfaceView == null && surface == null) return


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
                videoDecoder = VideoDecoder(url, surfaceView, surface).apply {
                    stateListener = listener
                }
                threadPool.execute(videoDecoder)
                threadPool.execute(audioDecoder)
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

    fun seekTo(int: Int) {
        videoDecoder?.seekTo(int)
        audioDecoder?.seekTo(int)

    }

    enum class DecoderType {
        NativeDecoder,
        FFmpegDecoder
    }
}