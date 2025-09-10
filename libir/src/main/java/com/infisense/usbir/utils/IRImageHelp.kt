package com.infisense.usbir.utils

import android.graphics.Bitmap
import android.util.Log
import com.example.open3d.JNITool
import com.topdon.lib.core.bean.AlarmBean
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * utility，utility
 * @author: CaiSongL
 * @date: 2024/1/17 9:54
 */
class IRImageHelp {


    // Utility function
    @Volatile
    private var colorList: IntArray ?= null

    @Volatile
    private var places: FloatArray? = null

    private var isUseGray = true
    private var customMaxTemp = 0f
    private var customMinTemp = 0f
    private var maxRGB = IntArray(3)
    private var minRGB = IntArray(3)

    fun getColorList() : IntArray?{
        return colorList
    }

    /**
     * SettingsutilityPseudo-colorutility
     * @author: CaiSongL
     * @date: 2024/1/17 10:07
     */
    fun setColorList(
        colorList: IntArray?,
        places: FloatArray?,
        isUseGray: Boolean,
        customMaxTemp: Float,
        customMinTemp: Float
    ) {
        if (colorList == null) {
            this.isUseGray = true
        } else {
            this.isUseGray = isUseGray
        }
        this.colorList = colorList
        this.places = places
        if (colorList != null) {
            this.customMaxTemp = customMaxTemp
            this.customMinTemp = customMinTemp
            val maxColor = colorList[colorList.size - 1]
            val minColor = colorList[0]
            this.maxRGB[0] = maxColor shr 16 and 0xFF
            this.maxRGB[1] = maxColor shr 8 and 0xFF
            this.maxRGB[2] = maxColor and 0xFF
            this.minRGB[0] = minColor shr 16 and 0xFF
            this.minRGB[1] = minColor shr 8 and 0xFF
            this.minRGB[2] = minColor and 0xFF
        }
    }


    /**
     * utilityPseudo-colorutility，utility，utilityPseudo-colorutility utilitysetColorListutilitySettings
     * @param imageDst ByteArray ： utility，argbutility
     * @param temperatureSrc ByteArray ： utility
     * @param imageWidth Int ：
     * @param imageHeight Int
     * @return ByteArray ： utility，argbutility
     */
    fun customPseudoColor(imageDst: ByteArray, temperatureSrc:ByteArray, imageWidth : Int, imageHeight : Int) : ByteArray{
        try {
            if (colorList != null && temperatureSrc != null) {
                var j = 0
                val imageDstLength: Int = imageWidth * imageHeight * 4
                // utility，utility
                var index = 0
                while (index < imageDstLength) {
                    // utility
                    var temperature0: Float =
                        ((temperatureSrc.get(j).toInt() and 0xff) + (temperatureSrc.get(j + 1)
                            .toInt() and 0xff) * 256).toFloat()
                    temperature0 = (temperature0 / 64 - 273.15).toFloat()
                    if (temperature0 >= customMinTemp && temperature0 <= customMaxTemp) {
                        // OpencvTools disabled due to missing AAR dependencies
                        // Using simple fallback color logic
                        /*
                        val rgb = OpencvTools.getOneColorByTempUnif(
                            customMaxTemp,
                            customMinTemp,
                            temperature0,
                            colorList,
                            places
                        )
                        if (rgb != null) {
                            imageDst[index] = rgb[0].toByte()
                            imageDst[index + 1] = rgb[1].toByte()
                            imageDst[index + 2] = rgb[2].toByte()
                        }
                        */
                        // Simple fallback: use temperature-based grayscale
                        val intensity = ((temperature0 - customMinTemp) / (customMaxTemp - customMinTemp) * 255).toInt().coerceIn(0, 255)
                        imageDst[index] = intensity.toByte()
                        imageDst[index + 1] = intensity.toByte()
                        imageDst[index + 2] = intensity.toByte()
                    } else if (temperature0 > customMaxTemp) {
                        if (isUseGray) {
                        } else {
                            imageDst[index] = maxRGB[0].toByte()
                            imageDst[index + 1] = maxRGB[1].toByte()
                            imageDst[index + 2] = maxRGB[2].toByte()
                        }
                    } else if (temperature0 < customMinTemp) {
                        if (isUseGray) {
                        } else {
                            imageDst[index] = minRGB[0].toByte()
                            imageDst[index + 1] = minRGB[1].toByte()
                            imageDst[index + 2] = minRGB[2].toByte()
                        }
                    }
                    imageDst[index + 3] = 255.toByte()
                    index += 4
                    j += 2
                }
//                                        Log.w("utility-utility", System.currentTimeMillis() - startTimeAll + "//");
            }
        } catch (exception: Exception) {
            Log.e("utility", exception.message!!)
        }finally {
            return imageDst
        }
    }



    /**
     * utility,utilityPseudo-colorutility
     */
    fun setPseudoColorMaxMin(imageDst: ByteArray?, temperatureSrc:ByteArray?,max : Float,
                       min : Float,imageWidth : Int,imageHeight : Int){
        if (temperatureSrc != null && (max != Float.MAX_VALUE || min != Float.MIN_VALUE)) {
            var j = 0
            val imageDstLength: Int = imageWidth * imageHeight * 4
            val biaochiMax: Float = max
            val biaochiMin: Float = min // utility
            val startTimeAll = System.currentTimeMillis()
            // utility，utility
            var index = 0
            while (index < imageDstLength) {

                // utility
                var temperature0: Float =
                    ((temperatureSrc[j].toInt() and 0xff) + (temperatureSrc[j + 1]
                        .toInt() and 0xff) * 256).toFloat()
                temperature0 = (temperature0 / 64 - 273.15).toFloat()
                val y0: Int = imageDst!![j].toInt() and 0xff
                if (temperature0 < biaochiMin || temperature0 > biaochiMax) {
                    val r: Int = imageDst!![index].toInt() and 0xff
                    val g: Int = imageDst!![index + 1].toInt() and 0xff
                    val b: Int = imageDst!![index + 2].toInt() and 0xff
                    // Utility function
                    val grey = (r * 0.3f + g * 0.59f + b * 0.11f).toInt()
                    imageDst!![index] = grey.toByte()
                    imageDst!![index + 1] = grey.toByte()
                    imageDst!![index + 2] = grey.toByte()
                }
                imageDst!![index + 3] = 255.toByte()
                index += 4
                j += 2
            }
        }
    }
    /**
     * contourDetection utility
     */
    fun contourDetection(alarmBean : AlarmBean?,imageDst : ByteArray?,temperatureSrc : ByteArray?,
                         imageWidth : Int,imageHeight : Int) : ByteArray?{
        if (alarmBean != null && imageDst != null && temperatureSrc != null) {
            if (alarmBean.isMarkOpen && (
                        (alarmBean.highTemp != Float.MAX_VALUE && alarmBean.isHighOpen)  ||
                                (alarmBean.isLowOpen && alarmBean.lowTemp != Float.MIN_VALUE)
                    )) {
                try {
                    val matByteArray = JNITool.draw_edge_from_temp_reigon_bitmap_argb_psd(imageDst,
                        temperatureSrc,
                        imageHeight,
                        imageWidth,
                        if (alarmBean.isHighOpen) alarmBean.highTemp else Float.MAX_VALUE,
                        if (alarmBean.isLowOpen) alarmBean.lowTemp else Float.MIN_VALUE,
                        alarmBean.highColor,
                        alarmBean.lowColor,
                        alarmBean.markType)
                    val diffMat = Mat(
                        imageHeight,
                        imageWidth,
                        CvType.CV_8UC3)
                    diffMat.put(0, 0, matByteArray)
                    Imgproc.cvtColor(diffMat, diffMat, Imgproc.COLOR_BGR2RGBA)
                    val grayData = ByteArray(diffMat.cols() * diffMat.rows() * 4)
                    diffMat[0, 0, grayData]
                    return grayData
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
        }
        return imageDst
    }



}