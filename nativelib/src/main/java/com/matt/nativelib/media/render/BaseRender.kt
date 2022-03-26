package com.matt.nativelib.media.render

import android.media.MediaCodec
import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 *
 * @author Liao Jianpeng
 * @Date 2022/3/26
 * @email 329524627@qq.com
 * @Description :
 */
abstract class BaseRender {
    /**
     * 初始化渲染器
     */
    abstract fun initRender(format: MediaFormat): Boolean

    /**
     * 渲染
     */
    abstract fun renderOneFrame(outputBuffer: ByteBuffer?, bufferInfo: MediaCodec.BufferInfo)

    /**
     * 释放
     */
    abstract fun release()

}