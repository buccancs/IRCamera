package com.guide.zm04c.matrix

import kotlin.experimental.and

class UsbBuffer {

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
     * 转无符号
     */
    private fun getMark(buf: ByteArray, offset: Int): Int {
        return (buf[offset].toUByte().toInt().shl(0) or ((buf[offset + 1].toUByte()).toInt()
            .shl(8)))
    }


    private fun isValidFrame(frame: ByteArray): Boolean {
        var i = 0
        while (i < frame.size - 1) {
            if (getMark(frame, i) == mark1) {
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
        //当前存储的buffer长度要大于4帧，才开始取数据
        if (mRingBuffer.getUnReadLength() < mFrameSize * 4) {
            return false
        }
        while (findHeadFramePos == -1 && mRingBuffer.getUnReadLength() > mFrameSize * 2) {
            mRingBuffer.read(mPakagebuffer, 0, mPakagebuffer.size)
            findHeadFramePos = if (mPakagebuffer != null && mPacketSize == mPakagebuffer.size) {
                //findHeadFrame = isValidFrame(mPakagebuffer);
                isValidFrameInt(mPakagebuffer)
            } else {
                break
            }
        }

        if (findHeadFramePos != -1) {
            //回退到找到帧头的那一包
            mRingBuffer.moveBack(mPacketSize - findHeadFramePos)
            //向前移动一帧数据
            mRingBuffer.moveForward(mFrameSize)
            mRingBuffer.read(mPakagebuffer, 0, mPacketSize)
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
                    lock.wait(100)//kotlin any没有wait()
                }
            } catch (e: InterruptedException) {
            }
        }
        return false
    }

    private val lock = Object()
}