package com.matt.nativelib.media.render

import android.media.MediaCodec
import android.media.MediaFormat
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.matt.nativelib.media.decoder.BaseDecoder

/**
 *
 * Created by Ljp
 * @Date: 2022/3/25
 * @email: 329524627@qq.com
 */
class VideoRender(private var surfaceView: SurfaceView? = null, private var surface: Surface? = null) : IVideoRender {
    private var mVideoWidth: Int = -1
    private var mVideoHeight: Int = -1

    override fun initRender(baseDecoder: BaseDecoder, codec: MediaCodec, format: MediaFormat): Boolean {
        mVideoWidth = format.getInteger(MediaFormat.KEY_WIDTH)
        mVideoHeight = format.getInteger(MediaFormat.KEY_HEIGHT)

        when {
            surface != null -> {
                codec.configure(format, surface, null, 0)
            }
            surfaceView?.holder?.surface != null -> {
                surface = surfaceView!!.holder.surface
                codec.configure(format, surface, null, 0)
            }
            else -> {
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
                        codec.configure(format, surface, null, 0)
                        baseDecoder.notifyDecode()
                    }
                })
                return false
            }
        }
        return true
    }

    override fun surfaceCreated(id: Int) {

    }

    override fun surfaceChanged(w: Int, h: Int) {

    }

    override fun drawFrame() {

    }

    override fun translate(dx: Float, dy: Float) {

    }

    override fun release() {

    }
}