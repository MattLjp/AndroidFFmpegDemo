package com.matt.androidffmpegdemo

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.matt.androidffmpegdemo.databinding.ActivitySimplePlayerBinding
import com.matt.videolibrary.media.decoder.AudioDecoder
import com.matt.videolibrary.media.decoder.VideoDecoder
import com.matt.videolibrary.media.muxer.MP4Repack
import java.util.concurrent.Executors


/**
 * 简单播放器页面
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 * @Datetime 2019-10-12 09:33
 *
 */
class SimplePlayerActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySimplePlayerBinding
    val path = Environment.getExternalStorageDirectory().absolutePath + "/mvtest.mp4"
    lateinit var videoDecoder: VideoDecoder
    lateinit var audioDecoder: AudioDecoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimplePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPlayer()
    }

    private fun initPlayer() {
        val threadPool = Executors.newFixedThreadPool(10)

        videoDecoder = VideoDecoder(path, binding.sfv, null)
        threadPool.execute(videoDecoder)

        audioDecoder = AudioDecoder(path)
        threadPool.execute(audioDecoder)

        videoDecoder.goOn()
        audioDecoder.goOn()
    }

    fun clickRepack(view: View) {
        repack()
    }

    private fun repack() {
        val repack = MP4Repack(path)
        repack.start()
    }

    override fun onDestroy() {
        videoDecoder.stop()
        audioDecoder.stop()
        super.onDestroy()
    }
}
