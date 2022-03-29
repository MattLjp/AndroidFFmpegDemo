/**
 *
 * Created by 公众号：字节流动 on 2021/3/16.
 * https://github.com/githubhaohao/LearnFFmpeg
 * 最新文章首发于公众号：字节流动，有疑问或者技术交流可以添加微信 Byte-Flow ,领取视频教程, 拉你进技术交流群
 *
 */
package com.matt.androidffmpegdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.matt.androidffmpegdemo.databinding.ActivityMediaPlayerBinding
import com.matt.nativelib.ffmpeg.FFMediaPlayer
import com.matt.nativelib.ffmpeg.FFMediaPlayer.Companion.MEDIA_PARAM_VIDEO_DURATION
import com.matt.nativelib.ffmpeg.FFMediaPlayer.Companion.MEDIA_PARAM_VIDEO_HEIGHT
import com.matt.nativelib.ffmpeg.FFMediaPlayer.Companion.MEDIA_PARAM_VIDEO_WIDTH
import com.matt.nativelib.ffmpeg.FFMediaPlayer.Companion.MSG_DECODER_DONE
import com.matt.nativelib.ffmpeg.FFMediaPlayer.Companion.MSG_DECODER_INIT_ERROR
import com.matt.nativelib.ffmpeg.FFMediaPlayer.Companion.MSG_DECODER_READY
import com.matt.nativelib.ffmpeg.FFMediaPlayer.Companion.MSG_DECODING_TIME
import com.matt.nativelib.ffmpeg.FFMediaPlayer.Companion.MSG_REQUEST_RENDER
import com.matt.nativelib.ffmpeg.FFMediaPlayer.Companion.VIDEO_RENDER_ANWINDOW
import com.matt.nativelib.view.MySurfaceView


class FFmpegPlayerActivity : AppCompatActivity(), SurfaceHolder.Callback,
    FFMediaPlayer.EventCallback {
    private lateinit var binding: ActivityMediaPlayerBinding

    private lateinit var mSurfaceView: MySurfaceView
    private var mMediaPlayer: FFMediaPlayer? = null
    private lateinit var mSeekBar: SeekBar
    private var mIsTouch = false
    private val mVideoPath: String
        get() = externalCacheDir.toString() + "/byteflow/one_piece.mp4"

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mSurfaceView = binding.surfaceView
        mSurfaceView.holder.addCallback(this)
        mSeekBar = binding.seekBar

        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                mIsTouch = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Log.d(
                    TAG,
                    "onStopTrackingTouch() called with: progress = [" + seekBar.progress + "]"
                )
                if (mMediaPlayer != null) {
                    mMediaPlayer!!.seekToPosition(mSeekBar.progress.toFloat())
                    mIsTouch = false
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!hasPermissionsGranted(REQUEST_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, REQUEST_PERMISSIONS, PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!hasPermissionsGranted(REQUEST_PERMISSIONS)) {
                Toast.makeText(
                    this,
                    "We need the permission: WRITE_EXTERNAL_STORAGE",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    protected override fun onPause() {
        super.onPause()
    }

    protected override fun onDestroy() {
        super.onDestroy()
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        Log.d(TAG, "surfaceCreated() called with: surfaceHolder = [$surfaceHolder]")
        mMediaPlayer = FFMediaPlayer()
        mMediaPlayer!!.addEventCallback(this)
        mMediaPlayer!!.init(mVideoPath, VIDEO_RENDER_ANWINDOW, surfaceHolder.surface)
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, format: Int, w: Int, h: Int) {
        Log.d(
            TAG,
            "surfaceChanged() called with: surfaceHolder = [$surfaceHolder], format = [$format], w = [$w], h = [$h]"
        )
        mMediaPlayer!!.play()
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        Log.d(TAG, "surfaceDestroyed() called with: surfaceHolder = [$surfaceHolder]")
        mMediaPlayer!!.stop()
        mMediaPlayer!!.unInit()
    }

    override fun onPlayerEvent(msgType: Int, msgValue: Float) {
        Log.d(TAG, "onPlayerEvent() called with: msgType = [$msgType], msgValue = [$msgValue]")
        runOnUiThread(Runnable {
            when (msgType) {
                MSG_DECODER_INIT_ERROR -> {}
                MSG_DECODER_READY -> onDecoderReady()
                MSG_DECODER_DONE -> {}
                MSG_REQUEST_RENDER -> {}
                MSG_DECODING_TIME -> if (!mIsTouch) mSeekBar.progress = msgValue.toInt()
                else -> {}
            }
        })
    }

    private fun onDecoderReady() {
        val videoWidth = mMediaPlayer!!.getMediaParams(MEDIA_PARAM_VIDEO_WIDTH).toInt()
        val videoHeight = mMediaPlayer!!.getMediaParams(MEDIA_PARAM_VIDEO_HEIGHT).toInt()
        if (videoHeight * videoWidth != 0) mSurfaceView.setAspectRatio(videoWidth, videoHeight)
        val duration = mMediaPlayer!!.getMediaParams(MEDIA_PARAM_VIDEO_DURATION).toInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mSeekBar.min = 0
        }
        mSeekBar.max = duration
    }

    protected fun hasPermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    companion object {
        private const val TAG = "MediaPlayerActivity"
        private val REQUEST_PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private const val PERMISSION_REQUEST_CODE = 1
    }
}