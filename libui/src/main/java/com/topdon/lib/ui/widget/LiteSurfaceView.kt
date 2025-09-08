package com.topdon.lib.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.SurfaceView
import java.nio.ByteBuffer

/**
 * des:
 * author: CaiSongL
 * date: 2024/8/1 13:52
 **/
class LiteSurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SurfaceView(context, attrs) {

    /** mFinalImageWidth property */
    var mFinalImageWidth = 0

    /** mFinalImageHeight property */
    var mFinalImageHeight = 0

    /** tmpData property */
    var tmpData: ByteArray ?= null
    /** mIrRotateData property */
    var mIrRotateData: ByteArray ?= null

    /** imageBitmap property */
    var imageBitmap : Bitmap ?= null



    /**
     * Function description.
     */
    fun scaleBitmap() : Bitmap{
        try {
            if (tmpData == null) {
                tmpData = ByteArray(mIrRotateData!!.size)
            }
            System.arraycopy(mIrRotateData, 0, tmpData, 0, mIrRotateData!!.size)
            if (imageBitmap == null || imageBitmap!!.getWidth() != mFinalImageWidth) {
                imageBitmap =
                    Bitmap.createBitmap(mFinalImageWidth, mFinalImageHeight, Bitmap.Config.ARGB_8888)
            }
            imageBitmap?.copyPixelsFromBuffer(ByteBuffer.wrap(tmpData))
            return  Bitmap.createScaledBitmap(imageBitmap!!,
                measuredWidth, measuredHeight, true)
        }catch (e : Exception){
            return Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        }
    }

}