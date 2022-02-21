package com.matt.nativelib.opengl.render

import android.graphics.SurfaceTexture


/**
 * 渲染器接口
 *
 * @author Liao Jianpeng
 * @Date 2022/2/21
 * @email 329524627@qq.com
 * @Description :
 */
interface IVideoRender {
    fun surfaceCreated()

    fun surfaceChanged()

    fun drawFrame()

    fun setVideoSize(videoW: Int, videoH: Int)
    fun setWorldSize(worldW: Int, worldH: Int)
    fun setAlpha(alpha: Float)
    fun draw()
    fun setTextureID(id: Int)
    fun getSurfaceTexture(cb: (st: SurfaceTexture) -> Unit) {}
    fun release()
}