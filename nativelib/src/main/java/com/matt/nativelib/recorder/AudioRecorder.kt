package com.matt.nativelib.recorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log

class AudioRecorder(private val mRecorderCallback: AudioRecorderCallback) : Thread() {
    private var mAudioRecord: AudioRecord? = null
    override fun run() {
        val mMinBufferSize = AudioRecord.getMinBufferSize(
            DEFAULT_SAMPLE_RATE,
            DEFAULT_CHANNEL_LAYOUT,
            DEFAULT_SAMPLE_FORMAT
        )
        Log.d(TAG, "run() called mMinBufferSize=$mMinBufferSize")
        if (AudioRecord.ERROR_BAD_VALUE == mMinBufferSize) {
            mRecorderCallback.onError("parameters are not supported by the hardware.")
            return
        }
        mAudioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            DEFAULT_SAMPLE_RATE,
            DEFAULT_CHANNEL_LAYOUT,
            DEFAULT_SAMPLE_FORMAT,
            mMinBufferSize
        )
        try {
            mAudioRecord!!.startRecording()
        } catch (e: IllegalStateException) {
            mRecorderCallback.onError(e.message + " [startRecording failed]")
            return
        }
        val sampleBuffer = ByteArray(4096)
        try {
            while (!currentThread().isInterrupted) {
                val result = mAudioRecord!!.read(sampleBuffer, 0, 4096)
                if (result > 0) {
                    mRecorderCallback.onAudioData(sampleBuffer, result)
                }
            }
        } catch (e: Exception) {
            mRecorderCallback.onError(e.message)
        }
        mAudioRecord!!.release()
        mAudioRecord = null
    }

    interface AudioRecorderCallback {
        fun onAudioData(data: ByteArray?, dataSize: Int)
        fun onError(msg: String?)
    }

    companion object {
        private const val TAG = "AudioRecorder"
        private const val DEFAULT_SAMPLE_RATE = 44100
        private const val DEFAULT_CHANNEL_LAYOUT = AudioFormat.CHANNEL_IN_STEREO
        private const val DEFAULT_SAMPLE_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}