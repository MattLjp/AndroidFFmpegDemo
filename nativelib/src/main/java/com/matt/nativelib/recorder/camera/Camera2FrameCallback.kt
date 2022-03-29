package com.matt.nativelib.recorder.camera

interface Camera2FrameCallback {
    fun onPreviewFrame(data: ByteArray?, width: Int, height: Int)
    fun onCaptureFrame(data: ByteArray?, width: Int, height: Int)
}