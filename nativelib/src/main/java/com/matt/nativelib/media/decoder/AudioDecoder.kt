package com.matt.nativelib.media.decoder

import android.media.*
import android.view.Surface
import com.matt.nativelib.media.Frame
import com.matt.nativelib.media.extractor.IExtractor
import com.matt.nativelib.media.extractor.AudioExtractor
import com.matt.nativelib.media.render.IAudioRender
import java.nio.ByteBuffer

/**
 * 音频解码器
 *
 * @author Liao Jianpeng
 * @Date 2022/2/21
 * @email 329524627@qq.com
 * @Description :
 */
class AudioDecoder(url: String) : BaseDecoder(url) {
    /**
     * 渲染器
     */
    var audioVideoRender: IAudioRender? = null

    override fun check(): Boolean {
        return url.isNotEmpty() && audioVideoRender != null
    }

    override fun initExtractor(path: String): IExtractor {
        return AudioExtractor(path)
    }

    override fun configCodec(baseDecoder: BaseDecoder, codec: MediaCodec, format: MediaFormat): Boolean {
        codec.configure(format, null, null, 0)
        audioVideoRender?.initRender(format)
        return true
    }

    override fun decodeOneFrame(frame: Frame) {
        audioVideoRender?.renderOneFrame(frame)
    }

    override fun release() {
        audioVideoRender?.release()
    }


}