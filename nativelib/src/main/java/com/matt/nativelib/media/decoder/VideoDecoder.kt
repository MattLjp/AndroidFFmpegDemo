package com.matt.nativelib.media.decoder

import android.media.MediaCodec
import android.media.MediaFormat
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.matt.nativelib.media.Frame
import com.matt.nativelib.media.extractor.IExtractor
import com.matt.nativelib.media.extractor.VideoExtractor
import com.matt.nativelib.media.render.IVideoRender
import java.nio.ByteBuffer

/**
 * 视频解码器
 *
 * @author Liao Jianpeng
 * @Date 2022/2/21
 * @email 329524627@qq.com
 * @Description :
 */
class VideoDecoder(url: String) : BaseDecoder(url) {
    private val TAG = "VideoDecoder"

    /**
     * 渲染器
     */
    var videoVideoRender: IVideoRender? = null

    override fun check(): Boolean {
        return url.isNotEmpty() && videoVideoRender != null
    }

    override fun initExtractor(path: String): IExtractor {
        return VideoExtractor(path)
    }

    override fun configCodec(baseDecoder: BaseDecoder, codec: MediaCodec, format: MediaFormat): Boolean {
        return videoVideoRender?.initRender(baseDecoder, codec, format) ?: false
    }

    override fun decodeOneFrame(frame: Frame) {

    }

    override fun release() {
        videoVideoRender?.release()
    }


}