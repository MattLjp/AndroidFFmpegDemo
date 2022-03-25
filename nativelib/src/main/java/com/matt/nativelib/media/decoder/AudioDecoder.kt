package com.matt.nativelib.media.decoder

import android.media.*
import com.matt.nativelib.media.extractor.IExtractor
import com.matt.nativelib.media.extractor.AudioExtractor
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

    override fun check(): Boolean {
        return url.isNotEmpty()
    }

    override fun initExtractor(path: String): IExtractor {
        return AudioExtractor(path)
    }

    override fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean {
        codec.configure(format, null, null, 0)
        return true
    }

}