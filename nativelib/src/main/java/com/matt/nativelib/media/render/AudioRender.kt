package com.matt.nativelib.media.render

import android.media.*
import com.matt.nativelib.media.Frame
import java.nio.ByteBuffer

/**
 *
 * @author Liao Jianpeng
 * @Date 2022/2/22
 * @email 329524627@qq.com
 * @Description :
 */
class AudioRender : IAudioRender {
    /**音频播放器*/
    private var mAudioTrack: AudioTrack? = null

    /**音频数据缓存*/
    private var mAudioOutTempBuf: ShortArray? = null

    override fun initRender(format: MediaFormat): Boolean {
        try {
            val channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)

            val pcmEncodeBit = if (format.containsKey(MediaFormat.KEY_PCM_ENCODING)) {
                format.getInteger(MediaFormat.KEY_PCM_ENCODING)
            } else {
                //如果没有这个参数，默认为16位采样
                AudioFormat.ENCODING_PCM_16BIT
            }
            val channel = if (channels == 1) {
                //单声道
                AudioFormat.CHANNEL_OUT_MONO
            } else {
                //双声道
                AudioFormat.CHANNEL_OUT_STEREO
            }

            //获取最小缓冲区
            val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channel, pcmEncodeBit)

            mAudioOutTempBuf = ShortArray(minBufferSize / 2)

            val mAudioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,//播放类型：音乐
                sampleRate, //采样率
                channel, //通道
                pcmEncodeBit, //采样位数
                minBufferSize, //缓冲区大小
                AudioTrack.MODE_STREAM
            ) //播放模式：数据流动态写入，另一种是一次性写入

            mAudioTrack.play()
            return true
        } catch (e: Exception) {
        }
        return false
    }


    override fun renderOneFrame(frame: Frame) {

        if (mAudioOutTempBuf!!.size < frame.bufferInfo.size / 2) {
            mAudioOutTempBuf = ShortArray(frame.bufferInfo.size / 2)
        }
        frame.buffer?.position(0)
        frame.buffer?.asShortBuffer()?.get(mAudioOutTempBuf, 0, frame.bufferInfo.size / 2)
        mAudioTrack!!.write(mAudioOutTempBuf!!, 0, frame.bufferInfo.size / 2)
    }

    override fun release() {
        mAudioTrack?.stop()
        mAudioTrack?.release()
    }
}