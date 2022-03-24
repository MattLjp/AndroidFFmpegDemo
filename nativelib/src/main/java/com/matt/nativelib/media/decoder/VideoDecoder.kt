package com.matt.nativelib.media.decoder

import android.media.MediaCodec
import android.media.MediaFormat
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.matt.nativelib.media.extractor.IExtractor
import com.matt.nativelib.media.extractor.VideoExtractor
import java.nio.ByteBuffer

/**
 * 视频解码器
 *
 * @author Liao Jianpeng
 * @Date 2022/2/21
 * @email 329524627@qq.com
 * @Description :
 */
class VideoDecoder(
    url: String,
    private var surfaceView: SurfaceView?,
    private var surface: Surface?
) : BaseDecoder(url) {
    private val TAG = "VideoDecoder"

    override fun check(): Boolean {
        if (surfaceView == null && surface == null) {
            Log.e(TAG, "SurfaceView和Surface都为空，至少需要一个不为空")
            mStateListener?.decoderError(this, "显示器为空")
            return false
        }
        return true
    }

    override fun initExtractor(path: String): IExtractor {
        return VideoExtractor(path)
    }


    override fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean {
        if (surface != null) {
            codec.configure(format, surface, null, 0)
            notifyDecode()
        } else if (surfaceView?.holder?.surface != null) {
            surface = surfaceView!!.holder.surface
            configCodec(codec, format)
        } else {
            surfaceView?.holder?.addCallback(object : SurfaceHolder.Callback2 {
                override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                }

                override fun surfaceCreated(holder: SurfaceHolder) {
                    surface = holder.surface
                    configCodec(codec, format)
                }
            })
            return false
        }
        return true
    }
}