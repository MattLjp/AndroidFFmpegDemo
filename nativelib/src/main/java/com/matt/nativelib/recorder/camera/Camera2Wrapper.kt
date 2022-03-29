/**
 *
 * Created by 公众号：字节流动 on 2021/3/16.
 * https://github.com/githubhaohao/LearnFFmpeg
 * 最新文章首发于公众号：字节流动，有疑问或者技术交流可以添加微信 Byte-Flow ,领取视频教程, 拉你进技术交流群
 *
 */
package com.matt.nativelib.recorder.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.Handler
import android.os.HandlerThread
import android.util.AndroidRuntimeException
import android.util.Log
import android.util.Size
import android.view.Surface
import androidx.core.content.ContextCompat
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class Camera2Wrapper {
    private val THRESHOLD = 0.001f
    private var mCamera2FrameCallback: Camera2FrameCallback? = null
    private var mContext: Context
    private var mCameraManager: CameraManager? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null
    private var mPreviewRequest: CaptureRequest? = null
    private var mCameraDevice: CameraDevice? = null
    var cameraId: String? = null
        private set
    lateinit var supportCameraIds: Array<String>
        private set
    private var mPreviewImageReader: ImageReader? = null
    private var mCaptureImageReader: ImageReader? = null
    var sensorOrientation: Int? = null
        private set
    private val mCameraLock = Semaphore(1)
    private var mDefaultPreviewSize = Size(1280, 720)
    private var mDefaultCaptureSize = Size(1280, 720)
    private var mPreviewSurface: Surface? = null
    var previewSize: Size? = null
        private set
    var pictureSize: Size? = null
        private set
    var supportPreviewSize: List<Size>? = null
        private set
    var supportPictureSize: List<Size>? = null
        private set
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null
    private val mOnPreviewImageAvailableListener = OnImageAvailableListener { reader ->
        val image = reader.acquireLatestImage()
        if (image != null) {
            if (mCamera2FrameCallback != null) {
                mCamera2FrameCallback!!.onPreviewFrame(
                    CameraUtil.YUV_420_888_data(image),
                    image.width,
                    image.height
                )
            }
            image.close()
        }
    }
    private val mOnCaptureImageAvailableListener = OnImageAvailableListener { reader ->
        val image = reader.acquireLatestImage()
        if (image != null) {
            if (mCamera2FrameCallback != null) {
                mCamera2FrameCallback!!.onCaptureFrame(
                    CameraUtil.YUV_420_888_data(image),
                    image.width,
                    image.height
                )
            }
            image.close()
        }
    }

    constructor(context: Context) {
        mContext = context
        mCamera2FrameCallback = context as Camera2FrameCallback
        initCamera2Wrapper()
    }

    constructor(context: Context, callback: Camera2FrameCallback?) {
        mContext = context
        mCamera2FrameCallback = callback
        initCamera2Wrapper()
    }

    private fun initCamera2Wrapper() {
        mCameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            supportCameraIds = mCameraManager!!.cameraIdList
            if (checkCameraIdSupport(DEFAULT_CAMERA_ID.toString())) {
            } else {
                throw AndroidRuntimeException("Don't support the camera id: " + DEFAULT_CAMERA_ID)
            }
            cameraId = DEFAULT_CAMERA_ID.toString()
            getCameraInfo(cameraId)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun getCameraInfo(cameraId: String?) {
        var characteristics: CameraCharacteristics? = null
        try {
            characteristics = mCameraManager!!.getCameraCharacteristics(cameraId!!)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        val streamConfigs =
            characteristics!!.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        if (streamConfigs != null) {
            supportPreviewSize = Arrays.asList(
                *streamConfigs.getOutputSizes(
                    SurfaceTexture::class.java
                )
            )
            var supportDefaultSize = false
            var sameRatioSize: Size? = null
            var defaultRatio = mDefaultPreviewSize.width * 1.0f / mDefaultPreviewSize.height
            previewSize = supportPreviewSize!![supportPreviewSize!!.size / 2]
            for (size in supportPreviewSize!!) {
                Log.d(
                    TAG,
                    "initCamera2Wrapper() called mSupportPreviewSize " + size.width + "x" + size.height
                )
                val ratio = size.width * 1.0f / size.height
                if (abs(ratio - defaultRatio) < THRESHOLD) {
                    Log.d(
                        TAG,
                        "initCamera2Wrapper() called mSupportPreviewSize sameRatioSize == size" + size.width + "x" + size.height
                    )
                    sameRatioSize = size
                }
                if (mDefaultPreviewSize.width == size.width && mDefaultPreviewSize.height == size.height) {
                    Log.d(TAG, "initCamera2Wrapper() called supportDefaultSize ")
                    supportDefaultSize = true
                    break
                }
            }
            if (supportDefaultSize) {
                previewSize = mDefaultPreviewSize
            } else if (sameRatioSize != null) {
                previewSize = sameRatioSize
            }
            //Log.d(TAG, "initCamera2Wrapper() called mPreviewSize[w,h] = [" + mPreviewSize.getWidth() + "," + mPictureSize.getHeight() + "]");
            supportDefaultSize = false
            sameRatioSize = null
            defaultRatio = mDefaultCaptureSize.width * 1.0f / mDefaultCaptureSize.height
            supportPictureSize =
                Arrays.asList(*streamConfigs.getOutputSizes(ImageFormat.YUV_420_888))
            pictureSize = supportPictureSize!![0]
            for (size in supportPictureSize!!) {
                Log.d(
                    TAG,
                    "initCamera2Wrapper() called mSupportPictureSize " + size.width + "x" + size.height
                )
                val ratio = size.width * 1.0f / size.height
                if (Math.abs(ratio - defaultRatio) < THRESHOLD) {
                    sameRatioSize = size
                }
                if (mDefaultCaptureSize.width == size.width && mDefaultCaptureSize.height == size.height) {
                    supportDefaultSize = true
                    break
                }
            }
            if (supportDefaultSize) {
                pictureSize = mDefaultCaptureSize
            } else if (sameRatioSize != null) {
                pictureSize = sameRatioSize
            }
        }
        sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
        Log.d(TAG, "initCamera2Wrapper() called mSensorOrientation = " + sensorOrientation)
    }

    private fun checkCameraIdSupport(cameraId: String): Boolean {
        var isSupported = false
        for (id in supportCameraIds) {
            if (cameraId == id) {
                isSupported = true
            }
        }
        return isSupported
    }

    fun startCamera() {
        startBackgroundThread()
        if (mPreviewImageReader == null && previewSize != null) {
            mPreviewImageReader = ImageReader.newInstance(
                previewSize!!.width,
                previewSize!!.height,
                ImageFormat.YUV_420_888,
                2
            )
            mPreviewImageReader!!.setOnImageAvailableListener(
                mOnPreviewImageAvailableListener,
                mBackgroundHandler
            )
            mPreviewSurface = mPreviewImageReader!!.surface
        }
        if (mCaptureImageReader == null && pictureSize != null) {
            mCaptureImageReader = ImageReader.newInstance(
                pictureSize!!.width,
                pictureSize!!.height,
                ImageFormat.YUV_420_888,
                2
            )
            mCaptureImageReader!!.setOnImageAvailableListener(
                mOnCaptureImageAvailableListener,
                mBackgroundHandler
            )
        }
        openCamera()
    }

    fun startCamera(previewSurfaceTex: SurfaceTexture?) {
        startBackgroundThread()
        if (previewSurfaceTex != null) {
            previewSurfaceTex.setDefaultBufferSize(previewSize!!.width, previewSize!!.height)
            mPreviewSurface = Surface(previewSurfaceTex)
        } else {
            mPreviewImageReader = ImageReader.newInstance(
                previewSize!!.width,
                previewSize!!.height,
                ImageFormat.YUV_420_888,
                2
            )
            mPreviewImageReader!!.setOnImageAvailableListener(
                mOnPreviewImageAvailableListener,
                mBackgroundHandler
            )
            mPreviewSurface = mPreviewImageReader!!.surface
        }
        if (mCaptureImageReader == null && pictureSize != null) {
            mCaptureImageReader = ImageReader.newInstance(
                pictureSize!!.width,
                pictureSize!!.height,
                ImageFormat.YUV_420_888,
                2
            )
            mCaptureImageReader!!.setOnImageAvailableListener(
                mOnCaptureImageAvailableListener,
                mBackgroundHandler
            )
        }
        openCamera()
    }

    fun stopCamera() {
        if (mPreviewImageReader != null) {
            mPreviewImageReader!!.setOnImageAvailableListener(null, null)
        }
        if (mCaptureImageReader != null) {
            mCaptureImageReader!!.setOnImageAvailableListener(null, null)
        }
        closeCamera()
        stopBackgroundThread()
    }

    fun updatePreviewSize(size: Size?) {
        if (size != null && size != previewSize) {
            previewSize = size
            stopCamera()
            startCamera()
        }
    }

    fun updatePictureSize(size: Size?) {
        if (size != null && size != pictureSize) {
            pictureSize = size
            stopCamera()
            startCamera()
        }
    }

    fun updateCameraId(cameraId: String) {
        for (id in supportCameraIds) {
            if (id == cameraId && this.cameraId != cameraId) {
                this.cameraId = cameraId
                getCameraInfo(this.cameraId)
                stopCamera()
                startCamera()
                break
            }
        }
    }

    private fun openCamera() {
        Log.d(TAG, "openCamera() called")
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val manager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (!mCameraLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            manager.openCamera(cameraId!!, mStateCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Cannot access the camera.$e")
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
        }
    }

    fun closeCamera() {
        Log.d(TAG, "closeCamera() called")
        try {
            mCameraLock.acquire()
            if (null != mCameraCaptureSession) {
                mCameraCaptureSession!!.close()
                mCameraCaptureSession = null
            }
            if (null != mCameraDevice) {
                mCameraDevice!!.close()
                mCameraDevice = null
            }
            if (null != mPreviewImageReader) {
                mPreviewImageReader!!.close()
                mPreviewImageReader = null
            }
            if (null != mCaptureImageReader) {
                mCaptureImageReader!!.close()
                mCaptureImageReader = null
            }
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            mCameraLock.release()
        }
    }

    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraLock.release()
            mCameraDevice = cameraDevice
            createCaptureSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            mCameraLock.release()
            cameraDevice.close()
            mCameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            mCameraLock.release()
            cameraDevice.close()
            mCameraDevice = null
        }
    }

    private fun createCaptureSession() {
        try {
            if (null == mCameraDevice || null == mPreviewSurface || null == mCaptureImageReader) return
            mCameraDevice!!.createCaptureSession(
                Arrays.asList(mPreviewSurface, mCaptureImageReader!!.surface),
                mSessionStateCallback, mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, "createCaptureSession $e")
        }
    }

    private val mSessionStateCallback: CameraCaptureSession.StateCallback =
        object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                mCameraCaptureSession = session
                try {
                    mPreviewRequest = createPreviewRequest()
                    if (mPreviewRequest != null) {
                        session.setRepeatingRequest(mPreviewRequest!!, null, mBackgroundHandler)
                    } else {
                        Log.e(TAG, "captureRequest is null")
                    }
                } catch (e: CameraAccessException) {
                    Log.e(TAG, "onConfigured $e")
                }
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                Log.e(TAG, "onConfigureFailed")
            }
        }

    private fun createPreviewRequest(): CaptureRequest? {
        return if (null == mCameraDevice || mPreviewSurface == null) null else try {
            val builder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            builder.addTarget(mPreviewSurface!!)
            builder.build()
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.message!!)
            null
        }
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera2Background")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        if (mBackgroundThread != null) {
            mBackgroundThread!!.quitSafely()
            try {
                mBackgroundThread!!.join()
                mBackgroundThread = null
                mBackgroundHandler = null
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    fun capture() {
        if (mCameraDevice == null) return
        val captureBuilder: CaptureRequest.Builder
        try {
            captureBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(mCaptureImageReader!!.surface)

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(
                CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )

            // Orientation
            val CaptureCallback: CaptureCallback = object : CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    if (mPreviewRequest != null && mCameraCaptureSession != null) {
                        try {
                            mCameraCaptureSession!!.setRepeatingRequest(
                                mPreviewRequest!!,
                                null,
                                mBackgroundHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            mCameraCaptureSession!!.stopRepeating()
            mCameraCaptureSession!!.abortCaptures()
            mCameraCaptureSession!!.capture(captureBuilder.build(), CaptureCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun setDefaultPreviewSize(size: Size?) {
        if (size != null && size.width * size.height > 0) {
            mDefaultPreviewSize = size
            getCameraInfo(cameraId)
        }
    }

    fun setDefaultCaptureSize(size: Size?) {
        if (size != null && size.width * size.height > 0) {
            mDefaultCaptureSize = size
            getCameraInfo(cameraId)
        }
    }

    companion object {
        private const val TAG = "Camera2Wrapper"
        private const val DEFAULT_CAMERA_ID = 0
    }
}