//package com.matt.androidffmpegdemo.test
//
//import android.os.Bundle
//import android.os.Environment
//import android.os.Handler
//import android.view.Surface
//import androidx.appcompat.app.AppCompatActivity
//import com.matt.androidffmpegdemo.databinding.ActivityEglPlayerBinding
//import com.matt.nativelib.media.Frame
//import com.matt.nativelib.media.decoder.BaseDecoder
//import com.matt.nativelib.media.decoder.IDecoderStateListener
//import com.matt.nativelib.media.decoder.VideoDecoder
//import com.matt.nativelib.media.render.OpenGlRender
//import com.matt.nativelib.media.render.egl.CustomerGLRenderer
//import java.util.concurrent.Executors
//
//
///**
// * 使用自定义的OpenGL（EGL+Thread）渲染器，渲染多个视频画面的播放器
// *
// * @author Chen Xiaoping (562818444@qq.com)
// * @since LearningVideo
// * @version LearningVideo
// * @Datetime 2019-10-26 21:07
// *
// */
//class EGLPlayerActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityEglPlayerBinding
//    private val path = Environment.getExternalStorageDirectory().absolutePath + "/mvtest_2.mp4"
//    private val path2 = Environment.getExternalStorageDirectory().absolutePath + "/mvtest.mp4"
//
//    private val threadPool = Executors.newFixedThreadPool(10)
//
//    private var mRenderer = CustomerGLRenderer()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityEglPlayerBinding.inflate(layoutInflater)
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
//            videoDecoder.mStateListener = object : IDecoderStateListener {
//                override fun decoderReady(decodeJob: BaseDecoder?) {
//
//                }
//
//                override fun decoderRunning(decodeJob: BaseDecoder?) {
//
//                }
//
//                override fun decoderPause(decodeJob: BaseDecoder?) {
//
//                }
//
//                override fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame) {
//                    mRenderer.notifySwap(frame.bufferInfo.presentationTimeUs)
//                }
//
//                override fun decoderFinish(decodeJob: BaseDecoder?) {
//
//                }
//
//                override fun decoderDestroy(decodeJob: BaseDecoder?) {
//
//                }
//
//                override fun decoderError(decodeJob: BaseDecoder?, msg: String) {
//
//                }
//
//            }
//            videoDecoder.start()
//        }
//        val videoRender2 = OpenGlRender()
//        videoRender2.getSurfaceTexture {
//            val videoDecoder = VideoDecoder(path2, null, Surface(it))
//            videoDecoder.videoVideoRender = videoRender2
//            videoDecoder.mStateListener = object : IDecoderStateListener {
//                override fun decoderReady(decodeJob: BaseDecoder?) {
//
//                }
//
//                override fun decoderRunning(decodeJob: BaseDecoder?) {
//
//                }
//
//                override fun decoderPause(decodeJob: BaseDecoder?) {
//
//                }
//
//                override fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame) {
//                    mRenderer.notifySwap(frame.bufferInfo.presentationTimeUs)
//                }
//
//                override fun decoderFinish(decodeJob: BaseDecoder?) {
//
//                }
//
//                override fun decoderDestroy(decodeJob: BaseDecoder?) {
//
//                }
//
//                override fun decoderError(decodeJob: BaseDecoder?, msg: String) {
//
//                }
//
//            }
//            videoDecoder.start()
//        }
//        videoRender2.setAlpha(0.5f)
//        Handler().postDelayed({
//            videoRender2.scale(0.5f, 0.5f)
//        }, 1000)
//
//        mRenderer.addDrawer(videoRender1)
//        mRenderer.addDrawer(videoRender2)
//
//        mRenderer.setSurface(binding.sfv)
//    }
//}