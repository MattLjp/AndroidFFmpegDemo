//package com.matt.androidffmpegdemo.test
//
//import android.os.Bundle
//import android.os.Environment
//import android.os.Handler
//import android.view.Surface
//import androidx.appcompat.app.AppCompatActivity
//import com.matt.androidffmpegdemo.databinding.ActivityOpenglPlayerBinding
//import com.matt.nativelib.media.decoder.VideoDecoder
//import com.matt.nativelib.media.render.*
//
//
///**
// * 使用OpenGL渲染多个视频画面的播放器
// *
// * @author Chen Xiaoping (562818444@qq.com)
// * @since LearningVideo
// * @version LearningVideo
// * @Datetime 2019-10-26 21:07
// *
// */
//class MultiOpenGLPlayerActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityOpenglPlayerBinding
//
//    private val path = Environment.getExternalStorageDirectory().absolutePath + "/mvtest_2.mp4"
//    private val path2 = Environment.getExternalStorageDirectory().absolutePath + "/mvtest.mp4"
//
//    private val render = MultiGLRender()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityOpenglPlayerBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        initPlayer()
//    }
//
//
//    private fun initPlayer() {
//        val videoRender1 = OpenGlRender()
//        videoRender1.getSurfaceTexture {
//            val videoDecoder = VideoDecoder(path, null, Surface(it))
//            videoDecoder.videoVideoRender = videoRender1
//            videoDecoder.start()
//        }
//        val videoRender2 = OpenGlRender()
//        videoRender2.getSurfaceTexture {
//            val videoDecoder = VideoDecoder(path2, null, Surface(it))
//            videoDecoder.videoVideoRender = videoRender2
//            videoDecoder.start()
//        }
//        videoRender2.setAlpha(0.5f)
//        Handler().postDelayed({
//            videoRender2.scale(0.5f, 0.5f)
//        }, 1000)
//
//        render.addDrawer(videoRender1)
//        render.addDrawer(videoRender2)
//
//        binding.glSurface.setEGLContextClientVersion(2)
//        binding.glSurface.setRenderer(render)
//    }
//
//
//}