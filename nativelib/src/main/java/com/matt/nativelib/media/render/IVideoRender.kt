package com.matt.nativelib.media.render

import android.graphics.SurfaceTexture
import android.media.MediaCodec
import android.media.MediaFormat
import com.matt.nativelib.media.decoder.BaseDecoder

/**
 *
 * @author Liao Jianpeng
 * @Date 2022/2/22
 * @email 329524627@qq.com
 * @Description :
 */
interface IVideoRender {
    /**
     * 初始化渲染器
     */
    fun initRender(baseDecoder: BaseDecoder, codec: MediaCodec, format: MediaFormat): Boolean

    fun surfaceCreated(id: Int)

    fun surfaceChanged(w: Int, h: Int)

    fun drawFrame()

    fun translate(dx: Float, dy: Float)

    fun getSurfaceTexture(cb: (st: SurfaceTexture) -> Unit) {}

    /**
     * 释放
     */
    fun release()
}