package com.matt.androidffmpegdemo

import android.os.Bundle
import android.os.Environment
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.matt.androidffmpegdemo.databinding.ActivityOpenglPlayerBinding
import com.matt.videolibrary.media.decoder.AudioDecoder
import com.matt.videolibrary.media.decoder.VideoDecoder
import com.matt.videolibrary.opengl.SimpleRender
import com.matt.videolibrary.opengl.drawer.IDrawer
import com.matt.videolibrary.opengl.drawer.VideoDrawer
import java.util.concurrent.Executors


/**
 * 使用OpenGL渲染的播放器
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 * @Datetime 2019-10-26 21:07
 *
 */
class OpenGLPlayerActivity: AppCompatActivity() {
    private lateinit var binding: ActivityOpenglPlayerBinding
    val path = Environment.getExternalStorageDirectory().absolutePath + "/mvtest.mp4"
    lateinit var drawer: IDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenglPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRender()
    }

    private fun initRender() {
        drawer = VideoDrawer()
        drawer.setVideoSize(1920, 1080)
        drawer.getSurfaceTexture {
            initPlayer(Surface(it))
        }
        binding.glSurface.setEGLContextClientVersion(2)
        val render = SimpleRender()
        render.addDrawer(drawer)
        binding.glSurface.setRenderer(render)
    }

    private fun initPlayer(sf: Surface) {
        val threadPool = Executors.newFixedThreadPool(10)

        val videoDecoder = VideoDecoder(path, null, sf)
        threadPool.execute(videoDecoder)

        val audioDecoder = AudioDecoder(path)
        threadPool.execute(audioDecoder)

        videoDecoder.goOn()
        audioDecoder.goOn()
    }
}