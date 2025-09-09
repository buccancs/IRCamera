package com.topdon.module.thermal.ir.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceView
import com.energy.iruvc.sdkisp.LibIRProcess
import com.energy.iruvc.sdkisp.LibIRProcess.ImageRes_t
import com.energy.iruvc.utils.CommonParams.IRPROCSRCFMTType
import com.energy.iruvc.utils.CommonParams.PseudoColorType
import com.infisense.usbir.utils.IRImageHelp
import com.infisense.usbir.utils.OpencvTools
import com.infisense.usbir.utils.PseudocodeUtils
import com.topdon.lib.core.bean.AlarmBean
// import com.topdon.pseudo.bean.CustomPseudoBean  // Temporarily disabled - pseudo component dependency
import com.topdon.module.thermal.ir.bean.DataBean // Use local data bean instead
import java.nio.ByteBuffer

/**
 * [CN_TEXT] Hik [CN_TEXT] SurfaceView.
 *
 * Created by LCG on 2024/11/30.
 */
class HikSurfaceView : SurfaceView {
    companion object {
        /**
         * [CN_TEXT]
         */
        private const val MULTIPLE = 2
    }


    /**
     * [CN_TEXT]
     */
    var isOpenAmplify: Boolean = false
        set(value) {
            field = value
            val isPortrait = rotateAngle == 90 || rotateAngle == 270
            val width = (if (isPortrait) 192 else 256) * (if (value) MULTIPLE else 1)
            val height = (if (isPortrait) 256 else 192) * (if (value) MULTIPLE else 1)
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }

    /**
     * [CN_TEXT]RotateAngle，[CN_TEXT] 0、90、180、270，[CN_TEXT] 270
     */
    @Volatile
    var rotateAngle: Int = 270
        set(value) {
            field = value
            val isPortrait = value == 90 || value == 270
            val width = (if (isPortrait) 192 else 256) * (if (isOpenAmplify) MULTIPLE else 1)
            val height = (if (isPortrait) 256 else 192) * (if (isOpenAmplify) MULTIPLE else 1)
            bitmap.reconfigure(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        }

    /**
     * [CN_TEXT]，[CN_TEXT].
     */
    var alarmBean = AlarmBean()

    /**
     * [CN_TEXT]Low temperature[CN_TEXT]，[CN_TEXT]Celsius，MIN_VALUE [CN_TEXT]Settings
     */
    var limitTempMin = Float.MIN_VALUE
    /**
     * [CN_TEXT]High temperature[CN_TEXT]，[CN_TEXT]Celsius，MAX_VALUE [CN_TEXT]Settings
     */
    var limitTempMax = Float.MAX_VALUE


    /**
     * [CN_TEXT].
     */
    private val irImageHelp = IRImageHelp()

    /**
     * [CN_TEXT]
     */
    fun refreshCustomPseudo(it: DataBean) {
        // Temporarily disabled - pseudo component dependency
        // irImageHelp.setColorList(it.getColorList(), it.getPlaceList(), it.isUseGray, it.maxTemp, it.minTemp)
    }


    /**
     * Current[CN_TEXT]Pseudo-color.
     */
    @Volatile
    private var pseudoType: PseudoColorType = PseudoColorType.PSEUDO_3
    /**
     * SettingsCurrent[CN_TEXT]Pseudo-color[CN_TEXT]
     *
     * 1-White hot 3-Iron red 4-Rainbow1 5-Rainbow2 6-Rainbow3 7-Red hot 8-Hot iron 9-Rainbow4 10-Rainbow5 11-Black hot
     */
    fun setPseudoCode(code: Int) {
        pseudoType = PseudocodeUtils.changePseudocodeModeByOld(code)
    }


    /**
     * [CN_TEXT]Rotate[CN_TEXT].
     */
    private val imageRes = ImageRes_t()
    /**
     * Current[CN_TEXT] Bitmap.
     */
    private var bitmap: Bitmap = Bitmap.createBitmap(192, 256, Bitmap.Config.ARGB_8888)
    /**
     * [CN_TEXT]Rotate[CN_TEXT] ARGB [CN_TEXT].
     */
    private val sourceArgbArray = ByteArray(256 * 192 * 4)
    /**
     * Rotate[CN_TEXT] ARGB [CN_TEXT].
     */
    private val rotateArgbArray = ByteArray(256 * 192 * 4)
    /**
     * [CN_TEXT] ARGB [CN_TEXT].
     */
    private val amplifyArray = ByteArray(256 * MULTIPLE * 192 * MULTIPLE * 4)
    /**
     * [CN_TEXT]
     */
    private val tempArray = ByteArray(256 * 192 * 2)


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        imageRes.width = 256.toChar()
        imageRes.height = 192.toChar()
    }


    /**
     * [CN_TEXT]Current View [CN_TEXT].
     */
    fun getScaleBitmap(): Bitmap = synchronized(this) {
        Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    /**
     * [CN_TEXT]Specified[CN_TEXT] YUV [CN_TEXT]
     */
    fun refresh(yuvArray: ByteArray, newTempArray: ByteArray) {
        //[CN_TEXT]，[CN_TEXT]Rotate[CN_TEXT]
        val sourceWidth = 256
        val sourceHeight = 192

        System.arraycopy(newTempArray, 0, tempArray, 0, tempArray.size)

        //[CN_TEXT]White hotPseudo-color，[CN_TEXT]Mode[CN_TEXT]
        val pseudo: PseudoColorType = if (irImageHelp.getColorList() == null) pseudoType else PseudoColorType.PSEUDO_1
        LibIRProcess.convertYuyvMapToARGBPseudocolor(yuvArray, (sourceWidth * sourceHeight).toLong(), pseudo, sourceArgbArray)
        //[CN_TEXT]
        irImageHelp.customPseudoColor(sourceArgbArray, tempArray, sourceWidth, sourceHeight)
        //[CN_TEXT]
        irImageHelp.setPseudoColorMaxMin(sourceArgbArray, tempArray, limitTempMax, limitTempMin, sourceWidth, sourceHeight)
        //[CN_TEXT]
        val newArray = irImageHelp.contourDetection(alarmBean, sourceArgbArray, tempArray, sourceWidth, sourceHeight) ?: sourceArgbArray
        //Rotate
        when (rotateAngle) {
            90 -> LibIRProcess.rotateLeft90(newArray, imageRes, IRPROCSRCFMTType.IRPROC_SRC_FMT_ARGB8888, rotateArgbArray)
            180 -> LibIRProcess.rotate180(newArray, imageRes, IRPROCSRCFMTType.IRPROC_SRC_FMT_ARGB8888, rotateArgbArray)
            270 -> LibIRProcess.rotateRight90(newArray, imageRes, IRPROCSRCFMTType.IRPROC_SRC_FMT_ARGB8888, rotateArgbArray)
            else  -> System.arraycopy(newArray, 0, rotateArgbArray, 0, rotateArgbArray.size)
        }
        //[CN_TEXT]
        if (isOpenAmplify) {
            val width: Int = if (rotateAngle == 90 || rotateAngle == 270) sourceWidth else sourceHeight
            val height: Int = if (rotateAngle == 90 || rotateAngle == 270) sourceHeight else sourceWidth
            OpencvTools.supImage(rotateArgbArray, width, height, amplifyArray)
        }

        synchronized(this) {
            bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(if (isOpenAmplify) amplifyArray else rotateArgbArray))
        }

        val canvas: Canvas = holder.lockCanvas() ?: return
        canvas.drawBitmap(bitmap, null, Rect(0, 0, width, height), null)
        holder.unlockCanvasAndPost(canvas)
    }
}