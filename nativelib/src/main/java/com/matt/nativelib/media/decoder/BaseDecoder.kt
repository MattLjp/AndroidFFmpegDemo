package com.matt.nativelib.media.decoder

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import com.matt.nativelib.media.Frame
import com.matt.nativelib.media.extractor.IExtractor
import kotlinx.coroutines.*
import java.io.File
import java.nio.ByteBuffer

/**
 * 解码器基类
 *
 * @author Liao Jianpeng
 * @Date 2022/2/21
 * @email 329524627@qq.com
 * @Description :
 */
abstract class BaseDecoder(private val url: String) {

    private val TAG = "BaseDecoder"

    enum class DecodeState {
        /**开始状态*/
        START,

        /**解码中*/
        DECODING,

        /**解码暂停*/
        PAUSE,

        /**解码器释放*/
        STOP
    }

    //-------------线程相关------------------------

    /**
     * 线程等待锁
     */
    private val mLock = Object()


    //---------------状态相关-----------------------
    /**
     * 音视频解码器
     */
    private var mCodec: MediaCodec? = null

    /**
     * 音视频数据读取器
     */
    private var mExtractor: IExtractor? = null

    /**
     * 解码数据信息
     */
    private var mBufferInfo = MediaCodec.BufferInfo()

    private var mState = DecodeState.STOP

    var mStateListener: IDecoderStateListener? = null

    /**
     * 流数据是否结束
     */
    private var mIsEOS = false

    protected var mVideoWidth = 0

    protected var mVideoHeight = 0

    private var mDuration: Long = 0

    private var mEndPos: Long = 0

    /**
     * 开始解码时间，用于音视频同步
     */
    private var mStartTimeForSync = -1L

    private val iOScope by lazy { CoroutineScope(Dispatchers.IO) }

    init {
        initDecoder()
    }

    private fun initDecoder() {
        //【解码步骤：1. 初始化，并启动解码器】
        if (url.isEmpty() || !File(url).exists()) {
            Log.w(TAG, "文件路径为空")
            mStateListener?.decoderError(this, "文件路径为空")
            return
        }

        if (!check()) return

        //初始化数据提取器
        mExtractor = initExtractor(url)
        if (mExtractor == null ||
            mExtractor!!.getFormat() == null
        ) {
            Log.e(TAG, "无法解析文件")
            return
        }

        //初始化参数
        if (!initParams()) return

        //初始化渲染器
        if (!initRender()) return

        //初始化解码器
        if (!initCodec()) return

        mState = DecodeState.START
        mStateListener?.decoderReady(this)
    }

    private fun initParams(): Boolean {
        try {
            val format = mExtractor!!.getFormat()!!
            mDuration = format.getLong(MediaFormat.KEY_DURATION) / 1000
            if (mEndPos == 0L) mEndPos = mDuration
            initSpecParams(mExtractor!!.getFormat()!!)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun initCodec(): Boolean {
        try {
            val type = mExtractor!!.getFormat()!!.getString(MediaFormat.KEY_MIME)
            mCodec = MediaCodec.createDecoderByType(type!!)
            if (!configCodec(mCodec!!, mExtractor!!.getFormat()!!)) {
                waitDecode()
            }
            mCodec!!.start()
        } catch (e: Exception) {
            return false
        }
        return true
    }


    private suspend fun decoding() {
        mState = DecodeState.DECODING
        mStateListener?.decoderRunning(this)
        Log.i(TAG, "开始解码")
        try {
            while (mState != DecodeState.STOP) {
                if (mState == DecodeState.PAUSE) {
                    Log.i(TAG, "进入等待：$mState")
                    waitDecode()
                    // ---------【同步时间矫正】-------------
                    //恢复同步的起始时间，即去除等待流失的时间
                    mStartTimeForSync = System.currentTimeMillis() - getCurTimeStamp()
                }

                if (mStartTimeForSync == -1L) {
                    mStartTimeForSync = System.currentTimeMillis()
                }

                //如果数据没有解码完毕，将数据推入解码器解码
                if (!mIsEOS) {
                    //【解码步骤：2. 将数据压入解码器输入缓冲】
                    mIsEOS = pushBufferToDecoder()
                }

                //【解码步骤：3. 将解码好的数据从缓冲区拉取出来】
                val index = pullBufferFromDecoder()
                if (index >= 0) {
                    // ---------【音视频同步】-------------
                    sleepRender()

                    //【解码步骤：4. 渲染】
                    val outputBuffer = mCodec!!.getOutputBuffer(index)
                    render(outputBuffer, mBufferInfo)

                    //将解码数据传递出去
                    val frame = Frame()
                    frame.buffer = outputBuffer
                    frame.setBufferInfo(mBufferInfo)
                    mStateListener?.decodeOneFrame(this, frame)

                    //【解码步骤：5. 释放输出缓冲】
                    mCodec!!.releaseOutputBuffer(index, true)

                }
                //【解码步骤：6. 判断解码是否完成】
                if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                    Log.i(TAG, "解码结束")
                    mState = DecodeState.STOP
                    mStateListener?.decoderFinish(this)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            doneDecode()
            release()
        }
    }


    private fun pushBufferToDecoder(): Boolean {
        val inputBufferIndex = mCodec!!.dequeueInputBuffer(1000)
        var isEndOfStream = false

        if (inputBufferIndex >= 0) {
            val inputBuffer = mCodec!!.getInputBuffer(inputBufferIndex)
            if (inputBuffer != null) {
                val sampleSize = mExtractor!!.readBuffer(inputBuffer)
                mCodec!!.queueInputBuffer(
                    inputBufferIndex, 0,
                    sampleSize, mExtractor!!.getCurrentTimestamp(), 0
                )
            } else {
                //如果数据已经取完，压入数据结束标志：MediaCodec.BUFFER_FLAG_END_OF_STREAM
                mCodec!!.queueInputBuffer(
                    inputBufferIndex, 0, 0,
                    0, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                )
                isEndOfStream = true
            }
        }
        return isEndOfStream
    }

    private fun pullBufferFromDecoder(): Int {
        // 查询是否有解码完成的数据，index >=0 时，表示数据有效，并且index为缓冲区索引
        when (val index = mCodec!!.dequeueOutputBuffer(mBufferInfo, 1000)) {
            MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {}
            MediaCodec.INFO_TRY_AGAIN_LATER -> {}

            else -> {
                return index
            }
        }
        return -1
    }

    private suspend fun sleepRender() {
        val passTime = System.currentTimeMillis() - mStartTimeForSync
        val curTime = getCurTimeStamp()
        if (curTime > passTime) {
            delay(curTime - passTime)

        }
    }

    private fun release() {
        try {
            Log.i(TAG, "解码停止，释放解码器")
            mState = DecodeState.STOP
            mIsEOS = false
            mExtractor?.stop()
            mCodec?.stop()
            mCodec?.release()
            mStateListener?.decoderDestroy(this)
        } catch (e: Exception) {
        }
    }

    /**
     * 解码线程进入等待
     */
    private fun waitDecode() {
        try {
            if (mState == DecodeState.PAUSE) {
                mStateListener?.decoderPause(this)
            }
            synchronized(mLock) {
                mLock.wait()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 通知解码线程继续运行
     */
    protected fun notifyDecode() {
        synchronized(mLock) {
            mLock.notifyAll()
        }
        if (mState == DecodeState.DECODING) {
            mStateListener?.decoderRunning(this)
        }
    }


    fun start() {
        if (mState == DecodeState.START) {
            iOScope.launch {
                //启动解码器
                decoding()
            }
        } else if (mState == DecodeState.PAUSE) {
            mState = DecodeState.DECODING
            notifyDecode()
        }
    }

    fun pause() {
        mState = DecodeState.PAUSE
    }


    fun seekTo(pos: Long): Long {
        return 0
    }

    fun seekAndPlay(pos: Long): Long {
        return 0
    }

    fun stop() {
        mState = DecodeState.STOP
        notifyDecode()
    }


    fun getWidth(): Int {
        return mVideoWidth
    }

    fun getHeight(): Int {
        return mVideoHeight
    }

    fun getDuration(): Long {
        return mDuration
    }

    fun getCurTimeStamp(): Long {
        return mBufferInfo.presentationTimeUs / 1000
    }


    fun getMediaFormat(): MediaFormat? {
        return mExtractor?.getFormat()
    }


    /**
     * 检查子类参数
     */
    abstract fun check(): Boolean

    /**
     * 初始化数据提取器
     */
    abstract fun initExtractor(path: String): IExtractor

    /**
     * 初始化子类自己特有的参数
     */
    abstract fun initSpecParams(format: MediaFormat)

    /**
     * 配置解码器
     */
    abstract fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean

    /**
     * 初始化渲染器
     */
    abstract fun initRender(): Boolean

    /**
     * 渲染
     */
    abstract fun render(
        outputBuffer: ByteBuffer?,
        bufferInfo: MediaCodec.BufferInfo
    )

    /**
     * 结束解码
     */
    abstract fun doneDecode()
}