package com.matt.nativelib.media.render

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.matt.nativelib.media.render.OpenGLTools
import com.matt.nativelib.media.render.OpenGlRender
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 * @author Liao Jianpeng
 * @Date 2022/2/22
 * @email 329524627@qq.com
 * @Description :
 */
class SimpleGLRender : GLSurfaceView.Renderer {
    var render: IVideoRender? = null
    var getSurfaceTexture: ((SurfaceTexture?) -> Unit) = {}

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //清屏
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        val textureIds = OpenGLTools.createTextureIds(1)
        render?.surfaceCreated(textureIds[0])
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        render?.surfaceChanged(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        render?.drawFrame()
    }
}