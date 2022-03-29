package com.matt.nativelib.ffmpeg

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.view.Surface

/**
 *
 * @author Liao Jianpeng
 * @Date 2022/2/2
 * @email 329524627@qq.com
 * @Description :
 */
class FFMediaPlayer {

    private var mNativePlayerHandle: Long = 0

    private var mEventCallback: EventCallback? = null

    private var mAudioTrack: AudioTrack? = null

    fun createTrack(sampleRateInHz: Int, nb_channels: Int) {
        val channelConfig: Int
        channelConfig = if (nb_channels == 1) {
            AudioFormat.CHANNEL_OUT_MONO
        } else if (nb_channels == 2) {
            AudioFormat.CHANNEL_OUT_STEREO
        } else {
            AudioFormat.CHANNEL_OUT_MONO
        }
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRateInHz,
            channelConfig,
            AudioFormat.ENCODING_PCM_16BIT
        )
        mAudioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig,
            AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM
        )
        mAudioTrack!!.play()
    }

    fun playTrack(buffer: ByteArray?, lenght: Int) {
        if (mAudioTrack != null) {
            mAudioTrack!!.write(buffer!!, 0, lenght)
        }
    }


    fun init(url: String, videoRenderType: Int, surface: Surface) {
        mNativePlayerHandle = native_Init(url, FFMEDIA_PLAYER, videoRenderType, surface)
    }

    fun init(url: String, playerType: Int, videoRenderType: Int, surface: Surface) {
        mNativePlayerHandle = native_Init(url, playerType, videoRenderType, surface)
    }

    fun play() {
        native_Play(mNativePlayerHandle)
    }

    fun pause() {
        native_Pause(mNativePlayerHandle)
    }

    fun seekToPosition(position: Float) {
        native_SeekToPosition(mNativePlayerHandle, position)
    }

    fun stop() {
        native_Stop(mNativePlayerHandle)
    }

    fun unInit() {
        native_UnInit(mNativePlayerHandle)
        if (mAudioTrack != null) {
            mAudioTrack!!.stop()
            mAudioTrack!!.release()
        }
    }

    fun addEventCallback(callback: EventCallback?) {
        mEventCallback = callback
    }

    fun getMediaParams(paramType: Int): Long {
        return native_GetMediaParams(mNativePlayerHandle, paramType)
    }

    fun setMediaParams(paramType: Int, param: Any) {
        native_SetMediaParams(mNativePlayerHandle, paramType, param)
    }

    private fun playerEventCallback(msgType: Int, msgValue: Float) {
        if (mEventCallback != null) mEventCallback!!.onPlayerEvent(msgType, msgValue)
    }

    private external fun native_Init(
        url: String,
        playerType: Int,
        renderType: Int,
        surface: Any
    ): Long

    private external fun native_Play(playerHandle: Long)

    private external fun native_SeekToPosition(playerHandle: Long, position: Float)

    private external fun native_Pause(playerHandle: Long)

    private external fun native_Stop(playerHandle: Long)

    private external fun native_UnInit(playerHandle: Long)

    private external fun native_GetMediaParams(playerHandle: Long, paramType: Int): Long

    private external fun native_SetMediaParams(playerHandle: Long, paramType: Int, param: Any)

    companion object {
        init {
            System.loadLibrary("ffmpeglib")
        }

        //gl render type
        const val VIDEO_GL_RENDER = 0
        const val AUDIO_GL_RENDER = 1
        const val VR_3D_GL_RENDER = 2

        //player type
        const val FFMEDIA_PLAYER = 0
        const val HWCODEC_PLAYER = 1

        const val MSG_DECODER_INIT_ERROR = 0
        const val MSG_DECODER_READY = 1
        const val MSG_DECODER_DONE = 2
        const val MSG_REQUEST_RENDER = 3
        const val MSG_DECODING_TIME = 4

        const val MEDIA_PARAM_VIDEO_WIDTH = 0x0001
        const val MEDIA_PARAM_VIDEO_HEIGHT = 0x0002
        const val MEDIA_PARAM_VIDEO_DURATION = 0x0003

        const val MEDIA_PARAM_ASSET_MANAGER = 0x0020

        const val VIDEO_RENDER_OPENGL = 0
        const val VIDEO_RENDER_ANWINDOW = 1
        const val VIDEO_RENDER_3D_VR = 2

        fun getFFmpegVersion(): String? {
            return native_GetFFmpegVersion()
        }

        @JvmStatic
        private external fun native_GetFFmpegVersion(): String?

        //for GL render
        @JvmStatic
        external fun native_OnSurfaceCreated(renderType: Int)

        @JvmStatic
        external fun native_OnSurfaceChanged(renderType: Int, width: Int, height: Int)

        @JvmStatic
        external fun native_OnDrawFrame(renderType: Int)

        //update MVP matrix
        @JvmStatic
        external fun native_SetGesture(
            renderType: Int,
            xRotateAngle: Float,
            yRotateAngle: Float,
            scale: Float
        )

        @JvmStatic
        external fun native_SetTouchLoc(renderType: Int, touchX: Float, touchY: Float)
    }

    interface EventCallback {
        fun onPlayerEvent(msgType: Int, msgValue: Float)
    }
}