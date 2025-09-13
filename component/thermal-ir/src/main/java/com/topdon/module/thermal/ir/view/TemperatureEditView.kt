package com.topdon.module.thermal.ir.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.energy.iruvc.sdkisp.LibIRTemp
import com.energy.iruvc.utils.Line
import com.infisense.usbir.utils.TempDrawHelper.Companion.correct
import com.infisense.usbir.view.ITsTempListener
import java.lang.ref.WeakReference

/**
\12D  temperature View.
 */
/**
 * Custom Temperature edit view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
class TemperatureEditView : TemperatureBaseView {
    override var mode: Mode
        get() = super.mode
        set(value) {
            super.mode = value
            if (mode == Mode.CLEAR) {
                tempListData.pointTemps.clear()
                tempListData.lineTemps.clear()
                tempListData.rectangleTemps.clear()
                for (i in 0 until 3) {
                    val tmp = irtemp.TemperatureSampleResult()
                    tmp.type = -99
                    tempListData.pointTemps.add(tmp)
                    tempListData.lineTemps.add(tmp)
                    tempListData.rectangleTemps.add(tmp)
                }
            }
        }

/**
 * Temperature list utility class for thermal imaging operations.
 * Provides helper functions and common functionality.
 */
class TemperatureList {
        var pointTemps = arrayListOf<LibIRTemp.TemperatureSampleResult>()
        var lineTemps = arrayListOf<LibIRTemp.TemperatureSampleResult>()
        var rectangleTemps = arrayListOf<LibIRTemp.TemperatureSampleResult>()
    }

    var tempListData = TemperatureList()

    private var irtemp: LibIRTemp = LibIRTemp()
    private var irTempData: ByteArray = byteArrayOf()
    var fullInfo: LibIRTemp.TemperatureSampleResult? = null

    /**
\1display.
     */
    var isShowName = false
        set(value) {
            field = value
            invalidate()
        }

    private var iTsTempListenerWeakReference: WeakReference<ITsTempListener>? = null

    fun setITsTempListener(listener: ITsTempListener) {
        iTsTempListenerWeakReference = WeakReference(listener)
    }

    private fun getTSTemp(temp: Float): Float = iTsTempListenerWeakReference?.get()?.tempCorrectByTs(temp) ?: temp

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
    ) {
        tempListData.pointTemps.clear()
        tempListData.lineTemps.clear()
        tempListData.rectangleTemps.clear()
        for (i in 0 until 3) {
            val tmp = irtemp.TemperatureSampleResult()
            tmp.type = -99
            tempListData.pointTemps.add(tmp)
            tempListData.lineTemps.add(tmp)
            tempListData.rectangleTemps.add(tmp)
        }
    }

    override fun setImageSize(
        imageWidth: Int,
        imageHeight: Int,
    ) {
        super.setImageSize(imageWidth, imageHeight)
        irtemp = LibIRTemp(imageWidth, imageHeight)
    }

    fun setData(bytes: ByteArray) {
        irTempData = bytes
        irtemp.setTempData(irTempData)
        fullInfo = irtemp.getTemperatureOfRect(Rect(0, 0, imageWidth, imageHeight))
    }

    @SuppressLint("DrawAllocation"Test Data"P${index + 1}"Test Data"L${index + 1}"Test Data"R${index + 1}", rect)
        }
        return result
    }
}
