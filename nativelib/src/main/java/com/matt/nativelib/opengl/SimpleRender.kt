package com.matt.nativelib.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.matt.nativelib.opengl.render.IVideoRender
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 简单的OpenGL渲染器
 *
 * @author Liao Jianpeng
 * @Date 2022/2/21
 * @email 329524627@qq.com
 * @Description :
 */
class SimpleRender: GLSurfaceView.Renderer {

    private val videoRenders = mutableListOf<IVideoRender>()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //清屏
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        val textureIds = OpenGLTools.createTextureIds(videoRenders.size)
        videoRenders.forEachIndexed { index, render ->
            render.setTextureID(textureIds[index])
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        videoRenders.forEach {
            it.setWorldSize(width, height)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        videoRenders.forEach {
            it.draw()
        }
    }

    fun addDrawer(drawer: IVideoRender) {
        videoRenders.add(drawer)
    }
}