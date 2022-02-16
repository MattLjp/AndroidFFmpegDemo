package com.matt.androidffmpegdemo

import android.os.Bundle
import android.os.Environment
import android.view.Surface
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.matt.androidffmpegdemo.databinding.ActivitySynthesizerBinding
import com.matt.videolibrary.media.Frame
import com.matt.videolibrary.media.IDecoder
import com.matt.videolibrary.media.decoder.AudioDecoder
import com.matt.videolibrary.media.decoder.BaseDecoder
import com.matt.videolibrary.media.decoder.DefDecodeStateListener
import com.matt.videolibrary.media.decoder.VideoDecoder
import com.matt.videolibrary.media.encoder.AudioEncoder
import com.matt.videolibrary.media.encoder.BaseEncoder
import com.matt.videolibrary.media.encoder.DefEncodeStateListener
import com.matt.videolibrary.media.encoder.VideoEncoder
import com.matt.videolibrary.media.muxer.MMuxer
import com.matt.videolibrary.opengl.drawer.VideoDrawer
import com.matt.videolibrary.opengl.egl.CustomerGLRenderer
import java.util.concurrent.Executors


/**
 * 合成器页面
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 *
 */
class SynthesizerActivity : AppCompatActivity(), MMuxer.IMuxerStateListener {
    private lateinit var binding: ActivitySynthesizerBinding
    private val path = Environment.getExternalStorageDirectory().absolutePath + "/mvtest.mp4"

    private val threadPool = Executors.newFixedThreadPool(10)

    private var renderer = CustomerGLRenderer()

    private var audioDecoder: IDecoder? = null
    private var videoDecoder: IDecoder? = null

    private lateinit var videoEncoder: VideoEncoder
    private lateinit var audioEncoder: AudioEncoder

    private var muxer = MMuxer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySynthesizerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        muxer.setStateListener(this)
    }

    fun onStartClick(view: View) {
        binding.btn.text = "正在编码"
        binding.btn.isEnabled = false
        initVideo()
        initAudio()
        initAudioEncoder()
        initVideoEncoder()
    }

    private fun initVideoEncoder() {
        // 视频编码器
        videoEncoder = VideoEncoder(muxer, 1920, 1080)

        renderer.setRenderMode(CustomerGLRenderer.RenderMode.RENDER_WHEN_DIRTY)
        renderer.setSurface(videoEncoder.getEncodeSurface()!!, 1920, 1080)

        videoEncoder.setStateListener(object : DefEncodeStateListener {
            override fun encoderFinish(encoder: BaseEncoder) {
                renderer.stop()
            }
        })
        threadPool.execute(videoEncoder)
    }

    private fun initAudioEncoder() {
        // 音频编码器
        audioEncoder = AudioEncoder(muxer)
        // 启动编码线程
        threadPool.execute(audioEncoder)
    }

    private fun initVideo() {
        val drawer = VideoDrawer() // SoulVideoDrawer()
        drawer.setVideoSize(1920, 1080)
        drawer.getSurfaceTexture {
            initVideoDecoder(path, Surface(it))
        }
        renderer.addDrawer(drawer)
    }

    private fun initVideoDecoder(path: String, sf: Surface) {
        videoDecoder?.stop()
        videoDecoder = VideoDecoder(path, null, sf).withoutSync()
        videoDecoder!!.setStateListener(object : DefDecodeStateListener {
            override fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame) {
                renderer.notifySwap(frame.bufferInfo.presentationTimeUs)
                videoEncoder.encodeOneFrame(frame)
            }

            override fun decoderFinish(decodeJob: BaseDecoder?) {
                videoEncoder.endOfStream()
            }
        })
        videoDecoder!!.goOn()

        //启动解码线程
        threadPool.execute(videoDecoder!!)
    }

    private fun initAudio() {
        audioDecoder?.stop()
        audioDecoder = AudioDecoder(path).withoutSync()
        audioDecoder!!.setStateListener(object : DefDecodeStateListener {

            override fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame) {
                audioEncoder.encodeOneFrame(frame)
            }

            override fun decoderFinish(decodeJob: BaseDecoder?) {
                audioEncoder.endOfStream()
            }
        })
        audioDecoder!!.goOn()

        //启动解码线程
        threadPool.execute(audioDecoder!!)
    }

    override fun onMuxerFinish() {
        runOnUiThread {
            binding.btn.isEnabled = true
            binding.btn.text = "编码完成"
        }

        audioDecoder?.stop()
        audioDecoder = null

        videoDecoder?.stop()
        videoDecoder = null
    }
}