package com.matt.nativelib.media.render

import android.content.Context
import android.graphics.PointF
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent


/**
 * 自定义GLSurfaceView
 *
 * @author Chen Xiaoping (562818444@qq.com)
 *
 */
class DefGLSurfaceView : GLSurfaceView {

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    private var mPrePoint = PointF()

    private var mDrawer: IVideoRender? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPrePoint.x = event.x
                mPrePoint.y = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (event.x - mPrePoint.x) / width
                val dy = (event.y - mPrePoint.y) / height
                mDrawer?.translate(dx, dy)
                mPrePoint.x = event.x
                mPrePoint.y = event.y
            }
        }
        return true
    }

    fun addDrawer(drawer: IVideoRender) {
        mDrawer = drawer
    }
}