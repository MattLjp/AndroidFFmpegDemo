package com.matt.nativelib.media.decoder

import com.matt.nativelib.media.Frame


/**
 * 解码状态回调接口
 *
 * @author Liao Jianpeng
 * @Date 2022/2/21
 * @email 329524627@qq.com
 * @Description :
 */
interface IDecoderStateListener {
    fun decoderReady(decodeJob: BaseDecoder?)
    fun decoderRunning(decodeJob: BaseDecoder?)
    fun decoderPause(decodeJob: BaseDecoder?)
    fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame, time: Long, pos: Long)
    fun decoderFinish(decodeJob: BaseDecoder?)
    fun decoderDestroy(decodeJob: BaseDecoder?)
    fun decoderError(decodeJob: BaseDecoder?, msg: String)
}