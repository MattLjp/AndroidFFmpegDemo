package com.matt.androidffmpegdemo

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.matt.androidffmpegdemo.databinding.ActivitySimplerRenderBinding
import com.matt.videolibrary.opengl.SimpleRender
import com.matt.videolibrary.opengl.drawer.BitmapDrawer
import com.matt.videolibrary.opengl.drawer.IDrawer
import com.matt.videolibrary.opengl.drawer.TriangleDrawer


/**
 * 简单渲染页面
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 * @Datetime 2019-10-09 09:23
 *
 */
class SimpleRenderActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySimplerRenderBinding
    private lateinit var drawer: IDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimplerRenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawer = if (intent.getIntExtra("type", 0) == 0) {
            TriangleDrawer()
        } else {
            BitmapDrawer(BitmapFactory.decodeResource(CONTEXT!!.resources, R.drawable.cover))
        }
        initRender(drawer)
    }

    private fun initRender(drawer: IDrawer) {
        binding.glSurface.setEGLContextClientVersion(2)
        val render = SimpleRender()
        render.addDrawer(drawer)
        binding.glSurface.setRenderer(render)
    }

    override fun onDestroy() {
        drawer.release()
        super.onDestroy()
    }
}