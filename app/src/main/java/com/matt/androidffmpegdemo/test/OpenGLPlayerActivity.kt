package com.matt.androidffmpegdemo.test

import android.os.Bundle
import android.os.Environment
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.matt.androidffmpegdemo.databinding.ActivityOpenglPlayerBinding
import com.matt.nativelib.media.decoder.AudioDecoder
import com.matt.nativelib.media.decoder.VideoDecoder
import com.matt.nativelib.media.render.AudioRender
import com.matt.nativelib.media.render.OpenGlRender
import com.matt.nativelib.media.render.SimpleGLRender
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
class OpenGLPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpenglPlayerBinding
    val path = Environment.getExternalStorageDirectory().absolutePath + "/mvtest.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenglPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPlayer()
    }


    private fun initPlayer() {
        val simpleGLRender = SimpleGLRender()
        val audioRender = AudioRender()
        val videoRender = OpenGlRender()
        videoRender.getSurfaceTexture {
            val videoDecoder = VideoDecoder(path, null, Surface(it))
            videoDecoder.audioVideoRender = audioRender
            videoDecoder.videoVideoRender = videoRender
            videoDecoder.start()
        }
        simpleGLRender.render = videoRender

        binding.glSurface.setEGLContextClientVersion(2)
        binding.glSurface.setRenderer(simpleGLRender)
    }

}