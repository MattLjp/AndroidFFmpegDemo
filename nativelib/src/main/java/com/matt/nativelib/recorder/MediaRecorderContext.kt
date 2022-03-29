package com.matt.nativelib.recorder

abstract class MediaRecorderContext {
    private val mNativeContextHandle: Long = 0
    protected external fun native_CreateContext()
    protected external fun native_DestroyContext()
    protected external fun native_Init(): Int
    protected external fun native_UnInit(): Int
    protected external fun native_StartRecord(
        recorderType: Int,
        outUrl: String?,
        frameWidth: Int,
        frameHeight: Int,
        videoBitRate: Long,
        fps: Int
    ): Int

    protected external fun native_OnAudioData(data: ByteArray?, len: Int)
    protected external fun native_OnPreviewFrame(
        format: Int,
        data: ByteArray?,
        width: Int,
        height: Int
    )

    protected external fun native_StopRecord(): Int
    protected external fun native_SetTransformMatrix(
        translateX: Float,
        translateY: Float,
        scaleX: Float,
        scaleY: Float,
        degree: Int,
        mirror: Int
    )

    protected external fun native_OnSurfaceCreated()
    protected external fun native_OnSurfaceChanged(width: Int, height: Int)
    protected external fun native_OnDrawFrame()
    protected external fun native_SetFilterData(
        index: Int,
        format: Int,
        width: Int,
        height: Int,
        bytes: ByteArray?
    )

    protected external fun native_SetFragShader(index: Int, str: String?)

    companion object {
        const val IMAGE_FORMAT_RGBA = 0x01
        const val IMAGE_FORMAT_NV21 = 0x02
        const val IMAGE_FORMAT_NV12 = 0x03
        const val IMAGE_FORMAT_I420 = 0x04
        const val RECORDER_TYPE_SINGLE_VIDEO = 0 //仅录制视频
        const val RECORDER_TYPE_SINGLE_AUDIO = 1 //仅录制音频
        const val RECORDER_TYPE_AV = 2 //同时录制音频和视频,打包成 MP4 文件
    }
}