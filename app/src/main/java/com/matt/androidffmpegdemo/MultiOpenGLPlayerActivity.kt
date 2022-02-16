package com.matt.androidffmpegdemo

import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.matt.androidffmpegdemo.databinding.ActivityOpenglPlayerBinding
import com.matt.videolibrary.media.decoder.AudioDecoder
import com.matt.videolibrary.media.decoder.VideoDecoder
import com.matt.videolibrary.opengl.SimpleRender
import com.matt.videolibrary.opengl.drawer.VideoDrawer
import java.util.concurrent.Executors


/**
 * 使用OpenGL渲染多个视频画面的播放器
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 * @Datetime 2019-10-26 21:07
 *
 */
class MultiOpenGLPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpenglPlayerBinding

    private val path = Environment.getExternalStorageDirectory().absolutePath + "/mvtest_2.mp4"
    private val path2 = Environment.getExternalStorageDirectory().absolutePath + "/mvtest.mp4"

    private val render = SimpleRender()

    private val threadPool = Executors.newFixedThreadPool(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenglPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFirstVideo()
        initSecondVideo()
        initRender()
    }

    private fun initFirstVideo() {
        val drawer = VideoDrawer()
        drawer.setVideoSize(1920, 1080)
        drawer.getSurfaceTexture {
            initPlayer(path, Surface(it), true)
        }
        render.addDrawer(drawer)
    }

    private fun initSecondVideo() {
        val drawer = VideoDrawer()
        drawer.setAlpha(0.5f)
        drawer.setVideoSize(1920, 1080)
        drawer.getSurfaceTexture {
            initPlayer(path2, Surface(it), false)
        }
        render.addDrawer(drawer)
        binding.glSurface.addDrawer(drawer)

        Handler().postDelayed({
            drawer.scale(0.5f, 0.5f)
        }, 1000)
    }

    private fun initPlayer(path: String, sf: Surface, withSound: Boolean) {
        val videoDecoder = VideoDecoder(path, null, sf)
        threadPool.execute(videoDecoder)
        videoDecoder.goOn()

        if (withSound) {
            val audioDecoder = AudioDecoder(path)
            threadPool.execute(audioDecoder)
            audioDecoder.goOn()
        }
    }

    private fun initRender() {
        binding.glSurface.setEGLContextClientVersion(2)
        binding.glSurface.setRenderer(render)
    }

}