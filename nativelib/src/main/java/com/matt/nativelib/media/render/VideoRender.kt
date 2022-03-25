package com.matt.nativelib.media.render

import android.media.MediaFormat

/**
 *
 * Created by Ljp
 * @Date: 2022/3/25
 * @email: 329524627@qq.com
 */
class VideoRender : IVideoRender {
    private var mVideoWidth: Int = -1
    private var mVideoHeight: Int = -1

    override fun initRender(format: MediaFormat): Boolean {
        mVideoWidth = format.getInteger(MediaFormat.KEY_WIDTH)
        mVideoHeight = format.getInteger(MediaFormat.KEY_HEIGHT)
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