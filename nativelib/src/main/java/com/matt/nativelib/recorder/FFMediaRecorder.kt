package com.matt.nativelib.recorder

import android.content.res.Resources
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.Renderer
import android.util.Log
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FFMediaRecorder : MediaRecorderContext(), Renderer {
    private var mGLSurfaceView: GLSurfaceView? = null
    fun init(surfaceView: GLSurfaceView?) { //for Video
        mGLSurfaceView = surfaceView
        mGLSurfaceView!!.setEGLContextClientVersion(2)
        mGLSurfaceView!!.setRenderer(this)
        mGLSurfaceView!!.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        native_CreateContext()
        native_Init()
    }

    fun init() { //for audio
        native_CreateContext()
        native_Init()
    }

    fun requestRender() {
        if (mGLSurfaceView != null) {
            mGLSurfaceView!!.requestRender()
        }
    }

    fun setTransformMatrix(degree: Int, mirror: Int) {
        Log.d(TAG, "setTransformMatrix() called with: degree = [$degree], mirror = [$mirror]")
        native_SetTransformMatrix(0f, 0f, 1f, 1f, degree, mirror)
    }

    fun startRecord(
        recorderType: Int,
        outUrl: String,
        frameWidth: Int,
        frameHeight: Int,
        videoBitRate: Long,
        fps: Int
    ) {
        Log.d(
            TAG,
            "startRecord() called with: recorderType = [$recorderType], outUrl = [$outUrl], frameWidth = [$frameWidth], frameHeight = [$frameHeight], videoBitRate = [$videoBitRate], fps = [$fps]"
        )
        native_StartRecord(recorderType, outUrl, frameWidth, frameHeight, videoBitRate, fps)
    }

    fun onPreviewFrame(format: Int, data: ByteArray, width: Int, height: Int) {
        Log.d(
            TAG,
            "onPreviewFrame() called with: data = [$data], width = [$width], height = [$height]"
        )
        native_OnPreviewFrame(format, data, width, height)
    }

    fun onAudioData(data: ByteArray, size: Int) {
        Log.d(TAG, "onAudioData() called with: data = [$data], size = [$size]")
        native_OnAudioData(data, size)
    }

    fun stopRecord() {
        Log.d(TAG, "stopRecord() called")
        native_StopRecord()
    }

    fun loadShaderFromAssetsFile(shaderIndex: Int, r: Resources) {
        var result: String? = null
        try {
            val `in` = r.assets.open("shaders/fshader_$shaderIndex.glsl")
            var ch = 0
            val baos = ByteArrayOutputStream()
            while (`in`.read().also { ch = it } != -1) {
                baos.write(ch)
            }
            val buff = baos.toByteArray()
            baos.close()
            `in`.close()
            result = String(buff, Charset.forName("UTF-8"))
            result = result.replace("\\r\\n".toRegex(), "\n")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        result?.let { setFragShader(shaderIndex, it) }
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        Log.d(TAG, "onSurfaceCreated() called with: gl = [$gl], config = [$config]")
        native_OnSurfaceCreated()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        Log.d(
            TAG,
            "onSurfaceChanged() called with: gl = [$gl], width = [$width], height = [$height]"
        )
        native_OnSurfaceChanged(width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        Log.d(TAG, "onDrawFrame() called with: gl = [$gl]")
        native_OnDrawFrame()
    }

    fun setFilterData(index: Int, format: Int, width: Int, height: Int, bytes: ByteArray?) {
        native_SetFilterData(index, format, width, height, bytes)
    }

    fun setFragShader(index: Int, str: String?) {
        native_SetFragShader(index, str)
    }

    fun unInit() {
        native_UnInit()
        native_DestroyContext()
    }

    companion object {
        private const val TAG = "CameraRender"
    }
}