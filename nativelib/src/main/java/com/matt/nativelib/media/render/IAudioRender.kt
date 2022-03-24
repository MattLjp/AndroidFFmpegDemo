package com.matt.nativelib.media.render

import android.media.MediaCodec
import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 *
 * @author Liao Jianpeng
 * @Date 2022/2/22
 * @email 329524627@qq.com
 * @Description :
 */
interface IAudioRender {
    /**
     * 初始化渲染器
     */
    fun initRender(format: MediaFormat): Boolean

    /**
     * 渲染
     */
    fun renderOneFrame(
        outputBuffer: ByteBuffer?,
        bufferInfo: MediaCodec.BufferInfo
    )

    /**
     * 释放
     */
    fun release()
}