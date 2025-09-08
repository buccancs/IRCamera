package com.guide.zm04c.matrix

import android.util.Log
import kotlin.experimental.and

class UsbBuffer {

    private val TAG = "UsbBuffer"
    private var mRingBuffer: RingBuffer
    private var mFrameSize = 0
    private var mark1 = 0
    private var mPacketSize = 0
    private var mPakagebuffer: ByteArray

    constructor(frameSize: Int, headSize: Int, count: Int) {
        mFrameSize = frameSize
        mPacketSize = headSize
        mRingBuffer = RingBuffer(mFrameSize * count)
        mPakagebuffer = ByteArray(mPacketSize)
    }

    fun setFrameMark(mark1: Int) {
        this.mark1 = mark1
    }

    fun write(buffer: ByteArray?, offset: Int, length: Int) {
        if (mRingBuffer != null) {
            mRingBuffer.write(buffer, offset, length)
        }
    }

    private var findHeadFrame = false
    private var findHeadFramePos = -1

    /**
     * [Chinese text]
     */
    private fun getMark(buf: ByteArray, offset: Int): Int {
        return (buf[offset].toUByte().toInt().shl(0) or ((buf[offset + 1].toUByte()).toInt()
            .shl(8)))
    }

    private fun isValidFrame(frame: ByteArray): Boolean {
        var i = 0
        while (i < frame.size - 1) {
            if (getMark(frame, i) == mark1) {
                // Log.d(TAG, "[Chinese text]...");
                return true
            }
            i += 2
        }
        return false
    }

    private fun isValidFrameInt(frame: ByteArray): Int {
        var i = 0
        while (i < frame.size - 1) {
            if (getMark(frame, i) == mark1) {
//                Log.d(TAG, "[Chinese text]...")
                return i
            }
            i += 2
        }
        return -1
    }

    fun readFrame(frame: ByteArray): Boolean {
        if (mRingBuffer == null) {
            return false
        }
        // [Chinese text]buffer[Chinese text]4[Chinese text], [Chinese text]start[Chinese text]
        if (mRingBuffer.getUnReadLength() < mFrameSize * 4) {
//            Logger.d(TAG, "RingBuffer <4");
            return false
        }
        while (findHeadFramePos == -1 && mRingBuffer.getUnReadLength() > mFrameSize * 2) {
            mRingBuffer.read(mPakagebuffer, 0, mPakagebuffer.size)
            findHeadFramePos = if (mPakagebuffer != null && mPacketSize == mPakagebuffer.size) {
                // findHeadFrame = isValidFrame(mPakagebuffer);
                isValidFrameInt(mPakagebuffer)
            } else {
                break
            }
        }

//        Log.d(TAG, "1 findHeadFrame=" + findHeadFrame);
        if (findHeadFramePos != -1) {
            // Log.d(TAG, "1: " + BaseDataTypeConvertUtils.Companion.byteArr2HexString(mPakagebuffer));
            // [Chinese text]
            mRingBuffer.moveBack(mPacketSize - findHeadFramePos)
            // [Chinese text]
            mRingBuffer.moveForward(mFrameSize)
            mRingBuffer.read(mPakagebuffer, 0, mPacketSize)
            // Log.d(TAG, "2: " + BaseDataTypeConvertUtils.Companion.byteArr2HexString(mPakagebuffer));
            findHeadFrame = if (mPakagebuffer != null && mPacketSize == mPakagebuffer.size) {
                isValidFrame(mPakagebuffer)
            } else {
                false
            }
            mRingBuffer.moveBack(mFrameSize + if (findHeadFrame) mPacketSize else 0)
            findHeadFramePos = -1
        }
        if (findHeadFrame) {
            mRingBuffer.read(frame, 0, frame.size)
            return true
        }
        while (mRingBuffer.getUnReadLength() < mFrameSize * 2) {
            try {
                synchronized(this) {
                    Log.d(TAG, "wait(100)")
                    lock.wait(100)// kotlin any[Chinese text]wait()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        return false
    }

    private val lock = Object()
}