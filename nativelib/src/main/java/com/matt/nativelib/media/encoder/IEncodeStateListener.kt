package com.matt.nativelib.media.encoder

/**
 * 编码状态回调接口
 *
 * @author Liao Jianpeng
 * @Date 2022/2/21
 * @email 329524627@qq.com
 * @Description :
 */
interface IEncodeStateListener {
    fun encodeReady(encoder: BaseEncoder)
    fun encodeRunning(encoder: BaseEncoder)
    fun encoderFinish(encoder: BaseEncoder)
}