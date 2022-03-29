package com.matt.androidffmpegdemo

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matt.androidffmpegdemo.adapter.MyRecyclerViewAdapter
import com.matt.androidffmpegdemo.databinding.ActivityMainBinding
import com.matt.androidffmpegdemo.util.CommonUtils
import com.matt.nativelib.ffmpeg.FFMediaPlayer
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val TAG = "MainActivity"
    private val REQUEST_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val PERMISSION_REQUEST_CODE = 1
    private val EXAMPLE_LIST = arrayOf(
        "FFmpeg + ANativeWindow player",
        "FFmpeg + OpenGL ES player",
        "FFmpeg + OpenSL ES visual audio",
        "FFmpeg + OpenGL ES VR player",
        "FFmpeg + single video recorder",
        "FFmpeg + single audio recorder",
        "FFmpeg + AV recorder",
        "FFmpeg + stream media player",
        "FFmpeg + MediaCodec player"
    )

    private val FF_ANATIVE_WINDOWS_EXAMPLE = 0
    private val FF_OPENGLES_EXAMPLE = 1
    private val FF_OPENGLES_AUDIO_VISUAL_EXAMPLE = 2
    private val FF_OPENGLES_VR_EXAMPLE = 3
    private val FF_X264_VIDEO_RECORDER = 4
    private val FF_FDK_AAC_AUDIO_RECORDER = 5
    private val FF_AV_RECORDER = 6
    private val FF_STREAM_MEDIA_PLAYER = 7
    private val FF_MEDIACODEC_PLAYER = 8

    private var mSampleSelectedIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textView.text = """
            FFmpeg 版本和编译配置信息
            
            ${FFMediaPlayer.getFFmpegVersion()}
            """.trimIndent()
        requestPermission()
    }

    private fun requestPermission() {
        val permissions = Permission.Group.STORAGE
        AndPermission.with(this)
            .runtime()
            .permission(REQUEST_PERMISSIONS)
            .onGranted {
                CommonUtils.copyAssetsDirToSDCard(this, "byteflow", externalCacheDir.toString())
            }
            .onDenied {
                Toast.makeText(this, "请打开权限，否则无法获取本地文件", Toast.LENGTH_SHORT).show()
            }
            .start()
    }

    override fun onResume() {
        super.onResume()
        mSampleSelectedIndex = -1
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_change_sample) {
            showSelectExampleDialog()
        }
        return true
    }

    private fun showSelectExampleDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val rootView: View = inflater.inflate(R.layout.sample_selected_layout, null)
        val dialog = builder.create()
        val confirmBtn = rootView.findViewById<Button>(R.id.confirm_btn)
        confirmBtn.setOnClickListener { dialog.cancel() }
        val resolutionsListView: RecyclerView = rootView.findViewById(R.id.resolution_list_view)
        val myPreviewSizeViewAdapter = MyRecyclerViewAdapter(this, Arrays.asList(*EXAMPLE_LIST))
        myPreviewSizeViewAdapter.selectIndex = mSampleSelectedIndex
        myPreviewSizeViewAdapter.addOnItemClickListener(object :
            MyRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val selectIndex: Int = myPreviewSizeViewAdapter.selectIndex
                myPreviewSizeViewAdapter.selectIndex = position
                myPreviewSizeViewAdapter.safeNotifyItemChanged(selectIndex)
                myPreviewSizeViewAdapter.safeNotifyItemChanged(position)
                mSampleSelectedIndex = position
                when (position) {
                    FF_ANATIVE_WINDOWS_EXAMPLE ->
                        startActivity(Intent(this@MainActivity, FFmpegPlayerActivity::class.java))
                    FF_OPENGLES_EXAMPLE ->
                        startActivity(Intent(this@MainActivity, GLMediaPlayerActivity::class.java))
                    FF_OPENGLES_AUDIO_VISUAL_EXAMPLE ->
                        startActivity(
                            Intent(this@MainActivity, AudioVisualMediaPlayerActivity::class.java)
                        )
                    FF_OPENGLES_VR_EXAMPLE -> startActivity(
                        Intent(this@MainActivity, VRMediaPlayerActivity::class.java)
                    )
                    FF_X264_VIDEO_RECORDER -> startActivity(
                        Intent(this@MainActivity, VideoRecorderActivity::class.java)
                    )
                    FF_FDK_AAC_AUDIO_RECORDER -> startActivity(
                        Intent(this@MainActivity, AudioRecorderActivity::class.java)
                    )
                    FF_AV_RECORDER ->
                        startActivity(
                            Intent(this@MainActivity, AVRecorderActivity::class.java)
                        )
                    FF_STREAM_MEDIA_PLAYER ->
                        startActivity(
                            Intent(this@MainActivity, StreamMediaPlayerActivity::class.java)
                        )
                    FF_MEDIACODEC_PLAYER ->
                        startActivity(
                            Intent(this@MainActivity, NativeMediaPlayerActivity::class.java)
                        )
                    else
                    -> {
                    }
                }
                dialog.cancel()
            }
        })
        val manager = LinearLayoutManager(this)
        manager.orientation = LinearLayoutManager.VERTICAL
        resolutionsListView.layoutManager = manager
        resolutionsListView.adapter = myPreviewSizeViewAdapter
        resolutionsListView.scrollToPosition(mSampleSelectedIndex)
        dialog.show()
        dialog.window!!.setContentView(rootView)
    }

}
