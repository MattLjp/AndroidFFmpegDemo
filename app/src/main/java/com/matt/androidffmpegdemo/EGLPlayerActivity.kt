package com.matt.androidffmpegdemo

import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.matt.androidffmpegdemo.databinding.ActivityEglPlayerBinding
import com.matt.videolibrary.media.Frame
import com.matt.videolibrary.media.decoder.AudioDecoder
import com.matt.videolibrary.media.decoder.BaseDecoder
import com.matt.videolibrary.media.decoder.DefDecodeStateListener
import com.matt.videolibrary.media.decoder.VideoDecoder
import com.matt.videolibrary.opengl.drawer.VideoDrawer
import com.matt.videolibrary.opengl.egl.CustomerGLRenderer
import java.util.concurrent.Executors


/**
 * 使用自定义的OpenGL（EGL+Thread）渲染器，渲染多个视频画面的播放器
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 * @Datetime 2019-10-26 21:07
 *
 */
class EGLPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEglPlayerBinding
    private val path = Environment.getExternalStorageDirectory().absolutePath + "/mvtest_2.mp4"
    private val path2 = Environment.getExternalStorageDirectory().absolutePath + "/mvtest.mp4"

    private val threadPool = Executors.newFixedThreadPool(10)

    private var mRenderer = CustomerGLRenderer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEglPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFirstVideo()
        initSecondVideo()
        setRenderSurface()
    }

    private fun initFirstVideo() {
        val drawer = VideoDrawer()
        drawer.setVideoSize(1920, 1080)
        drawer.getSurfaceTexture {
            initPlayer(path, Surface(it), true)
        }
        mRenderer.addDrawer(drawer)
    }

    private fun initSecondVideo() {
        val drawer = VideoDrawer()
        drawer.setAlpha(0.5f)
        drawer.setVideoSize(1920, 1080)
        drawer.getSurfaceTexture {
            initPlayer(path2, Surface(it), false)
        }
        mRenderer.addDrawer(drawer)

        Handler().postDelayed({
            drawer.scale(0.5f, 0.5f)
        }, 1000)
    }

    private fun initPlayer(path: String, sf: Surface, withSound: Boolean) {
        val videoDecoder = VideoDecoder(path, null, sf)
        threadPool.execute(videoDecoder)
        videoDecoder.goOn()
        videoDecoder.setStateListener(object : DefDecodeStateListener {
            override fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame) {
                mRenderer.notifySwap(frame.bufferInfo.presentationTimeUs)
            }
        })

        if (withSound) {
            val audioDecoder = AudioDecoder(path)
            threadPool.execute(audioDecoder)
            audioDecoder.goOn()
        }
    }

    private fun setRenderSurface() {
        mRenderer.setSurface(binding.sfv)
    }
}