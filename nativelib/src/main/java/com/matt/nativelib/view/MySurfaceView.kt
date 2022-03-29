package com.matt.nativelib.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceView

/**
 *
 * @Author:   Ljp
 * @Date:     2022/3/25
 * @Version:  1.0
 */
class MySurfaceView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    SurfaceView(context, attrs) {
    private var mRatioWidth = 0
    private var mRatioHeight = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height)
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth)
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height)
            }
        }
    }

    fun setAspectRatio(width: Int, height: Int) {
        Log.d(
            TAG,
            "setAspectRatio() called with: width = [$width], height = [$height]"
        )
        require(!(width < 0 || height < 0)) { "Size cannot be negative." }
        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    companion object {
        private const val TAG = "MySurfaceView"
    }
}
