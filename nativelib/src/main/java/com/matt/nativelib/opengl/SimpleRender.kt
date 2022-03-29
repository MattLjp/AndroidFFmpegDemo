package com.matt.nativelib.opengl

import android.opengl.GLSurfaceView
import com.matt.nativelib.ffmpeg.FFMediaPlayer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 简单的OpenGL渲染器
 * @Author:   Ljp
 * @Date:     2022/2/20
 * @Version:  1.0
 */
class SimpleRender : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        FFMediaPlayer.native_OnSurfaceCreated(FFMediaPlayer.VIDEO_GL_RENDER)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        FFMediaPlayer.native_OnSurfaceChanged(FFMediaPlayer.VIDEO_GL_RENDER, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        FFMediaPlayer.native_OnDrawFrame(FFMediaPlayer.VIDEO_GL_RENDER)
    }
}