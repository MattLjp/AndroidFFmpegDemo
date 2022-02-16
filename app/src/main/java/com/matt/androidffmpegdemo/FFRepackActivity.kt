package com.matt.androidffmpegdemo

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.matt.androidffmpegdemo.databinding.ActivityFfRepackBinding
import com.matt.videolibrary.ffmpeg.MediaPlayer
import kotlin.concurrent.thread


/**
 * FFmpeg 音视频重打包
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 * @Datetime 2020-08-02 14:27
 *
 */
class FFRepackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFfRepackBinding
    private var ffRepack: Int = 0

    private val srcPath = Environment.getExternalStorageDirectory().absolutePath + "/mvtest.mp4"
    private val destPath =
        Environment.getExternalStorageDirectory().absolutePath + "/mvtest_repack.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFfRepackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ffRepack = MediaPlayer.createRepack(srcPath, destPath)
    }

    fun onStartClick(view: View) {
        if (ffRepack != 0) {
            thread {
                MediaPlayer.startRepack(ffRepack)
            }
        }
    }


    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}