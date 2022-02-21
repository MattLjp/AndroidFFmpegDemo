package com.matt.nativelib.opengl

import android.opengl.GLES20

/**
 *
 * @author Liao Jianpeng
 * @Date 2022/2/21
 * @email 329524627@qq.com
 * @Description :
 */
object OpenGLManager {

    fun surfaceCreated() {
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        //开启混合，即半透明
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)


    }

    fun surfaceChanged() {

    }

    fun drawFrame() {

    }
}