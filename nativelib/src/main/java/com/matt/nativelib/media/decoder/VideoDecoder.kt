package com.matt.nativelib.media.decoder

import android.media.MediaCodec
import android.media.MediaFormat
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
class VideoDecoder(path: String, sfv: SurfaceView?, surface: Surface?): BaseDecoder(path) {
    private val TAG = "VideoDecoder"

    private val mSurfaceView = sfv
    private var mSurface = surface

    override fun check(): Boolean {
        if (mSurfaceView == null && mSurface == null) {
            Log.w(TAG, "SurfaceView和Surface都为空，至少需要一个不为空")
            stateListener?.decoderError(this, "显示器为空")
            return false
        }
        return true
    }

    override fun initExtractor(path: String): IExtractor {
        return VideoExtractor(path)
    }

    override fun initSpecParams(format: MediaFormat) {
    }

    override fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean {
        when {
            mSurface != null -> {
                codec.configure(format, mSurface , null, 0)
                notifyDecode()
            }
            mSurfaceView?.holder?.surface != null -> {
                mSurface = mSurfaceView.holder?.surface
                configCodec(codec, format)
            }
            else -> {
                mSurfaceView?.holder?.addCallback(object : SurfaceHolder.Callback2 {
                    override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
                    }

                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                    }

                    override fun surfaceCreated(holder: SurfaceHolder) {
                        mSurface = holder.surface
                        configCodec(codec, format)
                    }
                })

                return false
            }
        }
        return true
    }

    override fun initRender(): Boolean {
        return true
    }

    override fun render(outputBuffer: ByteBuffer,
                        bufferInfo: MediaCodec.BufferInfo) {
    }

    override fun doneDecode() {
    }
}