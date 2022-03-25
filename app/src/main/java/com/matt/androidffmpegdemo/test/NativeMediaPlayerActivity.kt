package com.matt.androidffmpegdemo.test

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.matt.androidffmpegdemo.databinding.ActivityNativeMediaPlayerBinding

class NativeMediaPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNativeMediaPlayerBinding

    private var mIsTouch = false
    private val mVideoPath =
        Environment.getExternalStorageDirectory().absolutePath + "/byteflow/one_piece.mp4"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNativeMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
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
                    mMediaPlayer.seekToPosition(mSeekBar.progress)
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

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    protected fun hasPermissionsGranted(permissions: Array<String?>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission!!)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    companion object {
        private const val TAG = "MediaPlayerActivity"
        private const val PERMISSION_REQUEST_CODE = 1
        private val REQUEST_PERMISSIONS = arrayOf<String?>(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}