package com.matt.nativelib.recorder.camera

import android.graphics.ImageFormat
import android.graphics.Point
import android.media.Image
import android.util.Size

object CameraUtil {
    @JvmStatic
    fun getFitInScreenSize(
        previewWidth: Int, previewHeight: Int, screenWidth: Int,
        screenHeight: Int
    ): Size {
        val res = Point(0, 0)
        val ratioPreview = previewWidth * 1f / previewHeight
        var ratioScreen = 0.0f

        //landscape
        if (screenWidth > screenHeight) {
            ratioScreen = screenWidth * 1f / screenHeight
            if (ratioPreview >= ratioScreen) {
                res.x = screenWidth
                res.y = (res.x * previewHeight * 1f / previewWidth).toInt()
            } else {
                res.y = screenHeight
                res.x = (res.y * previewWidth * 1f / previewHeight).toInt()
            }
            //portrait
        } else {
            ratioScreen = screenHeight * 1f / screenWidth
            if (ratioPreview >= ratioScreen) {
                res.y = screenHeight
                res.x = (res.y * previewHeight * 1f / previewWidth).toInt()
            } else {
                res.x = screenWidth
                res.y = (res.x * previewWidth * 1f / previewHeight).toInt()
            }
        }
        return Size(res.x, res.y)
    }

    fun YUV_420_888_data(image: Image): ByteArray {
        val imageWidth = image.width
        val imageHeight = image.height
        val planes = image.planes
        val data = ByteArray(
            imageWidth * imageHeight *
                    ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8
        )
        var offset = 0
        for (plane in planes.indices) {
            val buffer = planes[plane].buffer
            val rowStride = planes[plane].rowStride
            // Experimentally, U and V planes have |pixelStride| = 2, which
            // essentially means they are packed.
            val pixelStride = planes[plane].pixelStride
            val planeWidth = if (plane == 0) imageWidth else imageWidth / 2
            val planeHeight = if (plane == 0) imageHeight else imageHeight / 2
            if (pixelStride == 1 && rowStride == planeWidth) {
                // Copy whole plane from buffer into |data| at once.
                buffer[data, offset, planeWidth * planeHeight]
                offset += planeWidth * planeHeight
            } else {
                // Copy pixels one by one respecting pixelStride and rowStride.
                val rowData = ByteArray(rowStride)
                for (row in 0 until planeHeight - 1) {
                    buffer[rowData, 0, rowStride]
                    for (col in 0 until planeWidth) {
                        data[offset++] = rowData[col * pixelStride]
                    }
                }
                // Last row is special in some devices and may not contain the full
                // |rowStride| bytes of data.
                // See http://developer.android.com/reference/android/media/Image.Plane.html#getBuffer()
                buffer[rowData, 0, Math.min(rowStride, buffer.remaining())]
                for (col in 0 until planeWidth) {
                    data[offset++] = rowData[col * pixelStride]
                }
            }
        }
        return data
    }
}