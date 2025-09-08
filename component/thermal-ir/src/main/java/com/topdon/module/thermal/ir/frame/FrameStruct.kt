package com.topdon.module.thermal.ir.frame

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SizeUtils
import com.topdon.lib.core.bean.AlarmBean
import com.topdon.lib.core.bean.WatermarkBean
import com.topdon.lib.core.utils.ByteUtils
import com.topdon.lib.core.utils.ByteUtils.toBytes
import com.topdon.pseudo.bean.CustomPseudoBean

 * ```
 * len                 [ 0,  2)    2 byte    1024
 * name                [ 2, 18)   16 byte   ( TopInfrared TC001TS001  TC007)
 * ver                 [18, 26)    6 byte   APP(versionName)
 * width               [26, 28)    2 byte    256  192()
 * height              [28, 30)    2 byte    256  192()
 * rotate              [30, 32)    2 byte
 * pseudo              [32, 34)    2 byte
 * initRotate          [34, 36)    2 byte   ()
 * correctRotate       [36, 38)    2 byte   ()
 *                     [38, 81)   44 byte   (0)
 * customPseudoBean    [81,173)  92 byte
 *   colorSize                   81     1 byte
 *   selectIndex                 82     1 byte    index
 *   colors                [ 83,111)   28 byte   7
 *   zAltitudes            [111,118)    7 byte   7
 *   places                [118,146)   28 byte   7
 *   isUseCustomPseudo          146     1 byte
 *   maxTemp               [147,151)    4 byte
 *   minTemp               [151,155)    4 byte
 *   isColorCustom              155     1 byte
 *   customMinColor        [156,160)    4 byte
 *   customMiddleColor     [160,164)    4 byte
 *   customMaxColor        [164,168)    4 byte
 *   customRecommendIndex  [168,172)    4 byte
 *   isUseGray                  172     1 byte
 * isShowPseudoBar          173     1 byte
 * textColor           [174,178)    4 byte
 * watermarkBean       [178,628)  450 byte
 *   isOpen                 178     1 byte
 *   titleLen          [179,183)    4 byte
 *   title             [183,303)  120 byte
 *   addressLen        [303,307)    4 byte
 *   address           [307,627)  320 byte
 *   isAddTime              627     1 byte
 * alarmBean           [628,656)   28 byte
 *   isHighOpen             628     1 byte
 *   isLowOpen              629     1 byte
 *   highTemp          [630,634)    4 byte
 *   lowTemp           [634,638)    4 byte
 *   isMarkOpen             638     1 byte
 *   highColor         [639,643)    4 byte
 *   lowColor          [643,647)    4 byte
 *   markType          [647,651)    4 byte    1- 2
 *   isRingtoneOpen         651     1 byte
 *   ringtoneType      [652,656)    4 byte
 *   gainStatus       [657)    1 byte   // 1: 0:
 *   textSize         [658,659 2byte //
 *   environment      [660,663) 4byte //
 *   distance      [664,667) 4byte //
 *   radiation      [668,671) 4byte //
 *   amplify        672     1 byte   //
 * ```
class FrameStruct() {
    companion object {
         * .
        private const val SIZE = 1024

         * .
        fun toCode(
            name : String,
            width: Int,
            height: Int,
            rotate: Int,
            pseudo: Int,
            initRotate: Int,
            correctRotate: Int,
            customPseudoBean: CustomPseudoBean,
            isShowPseudoBar: Boolean,
            textColor: Int,
            watermarkBean: WatermarkBean,
            alarmBean: AlarmBean,
            gainStatus:Int,
            textSize:Int,
            environment:Float,
            distance : Float,
            radiation : Float,
            isAmplify : Boolean
        ): ByteArray {
            val resultArray = ByteArray(SIZE)

            resultArray[0] = (SIZE ushr 8).toByte()
            resultArray[1] = SIZE.toByte()

            // [2,18)
            val nameBytes = name.toBytes(16)
            System.arraycopy(nameBytes, 0, resultArray, 2, nameBytes.size)

            // [18,26)
            val verBytes = AppUtils.getAppVersionName().toBytes(8)
            System.arraycopy(verBytes, 0, resultArray, 18, verBytes.size)

            resultArray[26] = (width ushr 8).toByte()
            resultArray[27] = width.toByte()

            resultArray[28] = (height ushr 8).toByte()
            resultArray[29] = height.toByte()

            resultArray[30] = (rotate ushr 8).toByte()
            resultArray[31] = rotate.toByte()

            resultArray[32] = (pseudo ushr 8).toByte()
            resultArray[33] = pseudo.toByte()

            resultArray[34] = (initRotate ushr 8).toByte()
            resultArray[35] = initRotate.toByte()

            resultArray[36] = (correctRotate ushr 8).toByte()
            resultArray[37] = correctRotate.toByte()

            //[81,173)
            val customPseudoArray = customPseudoBean.toByteArray()
            System.arraycopy(customPseudoArray, 0, resultArray, 81, customPseudoArray.size)

            resultArray[173] = if (isShowPseudoBar) 1 else 0

            resultArray[174] = (textColor ushr 24).toByte()
            resultArray[175] = (textColor ushr 16).toByte()
            resultArray[176] = (textColor ushr 8).toByte()
            resultArray[177] = textColor.toByte()

            val watermarkArray = watermarkBean.toByteArray()
            System.arraycopy(watermarkArray, 0, resultArray, 178, watermarkArray.size)

            val alarmArray = alarmBean.toByteArray()
            System.arraycopy(alarmArray, 0, resultArray, 628, alarmArray.size)
            resultArray[657] = gainStatus.toByte()

            resultArray[658] = (textSize ushr 8).toByte()
            resultArray[659] = textSize.toByte()

            //  Float  4
            val envBytes = java.nio.ByteBuffer.allocate(4).putFloat(environment).array()
            val distanceBytes = java.nio.ByteBuffer.allocate(4).putFloat(distance).array()
            val radiationBytes = java.nio.ByteBuffer.allocate(4).putFloat(radiation).array()

            //  resultArray [660, 663)[664, 667)[668, 671)
            System.arraycopy(envBytes, 0, resultArray, 660, 4)
            System.arraycopy(distanceBytes, 0, resultArray, 664, 4)
            System.arraycopy(radiationBytes, 0, resultArray, 668, 4)

            resultArray[672] = if (isAmplify) 1 else 0
            return resultArray
        }
    }

    /** len property */
    var len = 0
    /** name property */
    var name: String = ""
    /** ver property */
    var ver: String = ""
    /** width property */
    var width = 0
    /** height property */
    var height = 0
    /** rotate property */
    var rotate = 0
    /** pseudo property */
    var pseudo = 0
    /** initRotate property */
    var initRotate = 0
    /** correctRotate property */
    var correctRotate = 0
    /** customPseudoBean property */
    var customPseudoBean = CustomPseudoBean()
    /** isShowPseudoBar property */
    var isShowPseudoBar = false
    /** textColor property */
    var textColor = 0xffffffff.toInt()
    /** watermarkBean property */
    var watermarkBean = WatermarkBean()
    /** alarmBean property */
    var alarmBean = AlarmBean()
    /** gainStatus property */
    var gainStatus : Int = 1 // 1: 0:
    /** textSize property */
    var textSize : Int = SizeUtils.sp2px(14f)
    /** environment property */
    var environment : Float = 0f
    /** distance property */
    var distance : Float = 0f
    /** radiation property */
    var radiation : Float = 0f
    /** isAmplify property */
    var isAmplify : Boolean = false



    constructor(data: ByteArray): this() {
        len = (data[0].toInt() and 0xff shl 8) or (data[1].toInt() and 0xff)

        // [2,18)
        var nameEndIndex = 17
        for (i in 17 downTo 2) {
            if (data[i].toInt() != 0) {
                nameEndIndex = i
                break
            }
        }
        val nameBytes = ByteArray(nameEndIndex - 1)
        System.arraycopy(data, 2, nameBytes, 0, nameBytes.size)
        name = String(nameBytes)

        // [18,26)
        val verBytes = ByteArray(8)
        System.arraycopy(data, 18, verBytes, 0, verBytes.size)
        ver = String(verBytes)

        width = (data[26].toInt() and 0xff shl 8) or (data[27].toInt() and 0xff)
        height = (data[28].toInt() and 0xff shl 8) or (data[29].toInt() and 0xff)
        rotate = (data[30].toInt() and 0xff shl 8) or (data[31].toInt() and 0xff)
        pseudo = (data[32].toInt() and 0xff shl 8) or (data[33].toInt() and 0xff)
        initRotate = (data[34].toInt() and 0xff shl 8) or (data[35].toInt() and 0xff)
        correctRotate = (data[36].toInt() and 0xff shl 8) or (data[37].toInt() and 0xff)

        // [81,173)
        val customPseudoArray = ByteArray(92)
        System.arraycopy(data, 81, customPseudoArray, 0, customPseudoArray.size)
        customPseudoBean = CustomPseudoBean.toCustomPseudoBean(customPseudoArray)

        isShowPseudoBar = data[173].toInt() == 1

        textColor = ByteUtils.bigBytesToInt(data[174], data[175], data[176], data[177])
        if (textColor == 0) {
            textColor = 0xffffffff.toInt()
        }

        val watermarkArray = ByteArray(450)
        System.arraycopy(data, 178, watermarkArray, 0, watermarkArray.size)
        watermarkBean = WatermarkBean.loadFromArray(watermarkArray)

        val alarmArray = ByteArray(28)
        System.arraycopy(data, 628, alarmArray, 0, alarmArray.size)
        alarmBean = AlarmBean.loadFromArray(alarmArray)
        gainStatus = data[657].toInt()
        val tmpTextSize = (data[658].toInt() and 0xff shl 8) or (data[659].toInt() and 0xff)
        if (tmpTextSize >= SizeUtils.sp2px(14f)){
            textSize = tmpTextSize
        }

        val envBytes = data.copyOfRange(660, 664)
        val distanceBytes = data.copyOfRange(664, 668)
        val radiationBytes = data.copyOfRange(668, 672)

        environment = java.nio.ByteBuffer.wrap(envBytes).float
        distance = java.nio.ByteBuffer.wrap(distanceBytes).float
        radiation = java.nio.ByteBuffer.wrap(radiationBytes).float
        isAmplify = data[672].toInt() == 1

    }

    /**
     * Function description.
     */
    fun isTC001(): Boolean = name == "TC001"
    
     * Check if device is TC007 (legacy compatibility - always false for TC001 only build)
    /**
     * Function description.
     */
    fun isTC007(): Boolean = false // TC001 only - no TC007 support

}