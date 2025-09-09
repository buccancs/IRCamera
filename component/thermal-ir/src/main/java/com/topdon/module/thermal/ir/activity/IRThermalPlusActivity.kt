package com.topdon.module.thermal.ir.activity

import android.graphics.Bitmap
import android.view.SurfaceView
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.energy.iruvc.sdkisp.LibIRProcess
import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.DualCameraParams
import com.infisense.usbdual.Const
import com.infisense.usbir.utils.IRImageHelp
import com.infisense.usbir.utils.PseudocodeUtils
import com.infisense.usbir.view.TemperatureView
import com.topdon.lib.core.bean.CameraItemBean
import com.topdon.lib.core.common.ProductType.PRODUCT_NAME_TCP
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.tools.ToastTools
import com.topdon.menu.constant.TwoLightType
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.event.GalleryAddEvent
import com.topdon.module.thermal.ir.video.VideoRecordFFmpeg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.nio.ByteBuffer


/**
 * Dual light[CN_TEXT]
 * @author: CaiSongL
 * @date: 2024/1/17 17:47
 */
// Legacy ARouter route annotation - now using NavigationManager
class IRThermalPlusActivity : BaseIRPlushActivity() {
    private val irImageHelp by lazy {
        IRImageHelp()
    }

    // Synthetic view properties - migrated from kotlin-android-extensions
    private val dualTextureViewNativeCamera by lazy { findViewById<SurfaceView>(R.id.dualTextureViewNativeCamera) }
    // // private val thermalSteeringView by lazy { findViewById<com.topdon.lib.ui.widget.SteeringWheelView>(R.id.thermalSteeringView) }  // ID doesn't exist
    // thermalRecyclerNight inherited from parent class


    override fun initContentView() = R.layout.activity_ir_thermal_double

    override fun isDualIR(): Boolean {
        return true
    }

    override fun getSurfaceView(): SurfaceView {
        return dualTextureViewNativeCamera
    }

    override fun getTemperatureDualView(): TemperatureView {
        return temperatureView
    }

    override fun getProductName(): String {
        return PRODUCT_NAME_TCP
    }

    override fun initView() {
        super.initView()
//        findViewById<TextView>(R.id.toolbar_title)?.text = "Dual light[CN_TEXT]"
        cameraView.visibility = View.GONE
        dualTextureViewNativeCamera?.visibility = View.VISIBLE
        // // thermalSteeringView.listener = { action, moveX ->
        //     setDisp(action, moveX)
        // }

        when (SaveSettingUtil.fusionType) {
            SaveSettingUtil.FusionTypeLPYFusion -> {//Dual light1
                thermalRecyclerNight?.twoLightType = TwoLightType.TWO_LIGHT_1
            }
            SaveSettingUtil.FusionTypeMeanFusion -> {//Dual light2
                thermalRecyclerNight?.twoLightType = TwoLightType.TWO_LIGHT_2
            }
            SaveSettingUtil.FusionTypeIROnly -> {//[CN_TEXT]Infrared
                thermalRecyclerNight?.twoLightType = TwoLightType.IR
            }
            SaveSettingUtil.FusionTypeVLOnly -> {//Visible light
                thermalRecyclerNight?.twoLightType = TwoLightType.LIGHT
            }
        }
    }




    /**
     * [CN_TEXT]Dual lightRegistration.
     * @param action -1[CN_TEXT] 1-[CN_TEXT] 0[CN_TEXT]
     * @param data CurrentRegistration[CN_TEXT]
     */
    private fun setDisp(action: Int, data: Int) {
        if (action == -1 || action == 1) {
            // [CN_TEXT]
            lifecycleScope.launch(Dispatchers.IO) {
                dualDisp = data
                dualView?.dualUVCCamera!!.setDisp(data)
            }
        } else {
            // [CN_TEXT]
            val oemInfo = ByteArray(1024)
            ircmd?.oemRead(CommonParams.ProductType.P2, oemInfo)
            val dataStr = data.toString()
            System.arraycopy(dataStr.toByteArray(), 0, oemInfo, 194, dataStr.toByteArray().size)
            val result = ircmd?.oemWrite(CommonParams.ProductType.P2,oemInfo)
//            SharedManager.setIrDualDisp(dualDisp)
            if (result == 0){
                // [CN_TEXT]
                // if (thermalSteeringView.isVisible) {
                //    thermalSteeringView.visibility = View.GONE
                    thermalRecyclerNight.setTwoLightSelected(TwoLightType.CORRECT, false)
                // }
            }else{
                ToastUtils.showShort(R.string.correction_fail)
            }

        }
    }

    override fun setTwoLight(twoLightType: TwoLightType, isSelected: Boolean) {
        when (twoLightType) {
            TwoLightType.TWO_LIGHT_1 -> {//Dual light1
                mCurrentFusionType = DualCameraParams.FusionType.LPYFusion
                SaveSettingUtil.fusionType = SaveSettingUtil.FusionTypeLPYFusion
                setFusion(mCurrentFusionType)
            }
            TwoLightType.TWO_LIGHT_2 -> {//Dual light2
                mCurrentFusionType = DualCameraParams.FusionType.MeanFusion
                SaveSettingUtil.fusionType = SaveSettingUtil.FusionTypeMeanFusion
                setFusion(mCurrentFusionType)
            }
            TwoLightType.IR -> {//[CN_TEXT]Infrared
                mCurrentFusionType = DualCameraParams.FusionType.IROnly
                SaveSettingUtil.fusionType = SaveSettingUtil.FusionTypeIROnly
                setFusion(mCurrentFusionType)
                thermalRecyclerNight.setTwoLightSelected(TwoLightType.CORRECT, false)
                // thermalSteeringView.visibility = View.GONE
            }
            TwoLightType.LIGHT -> {//[CN_TEXT]Visible light
                mCurrentFusionType = DualCameraParams.FusionType.VLOnly
                SaveSettingUtil.fusionType = SaveSettingUtil.FusionTypeVLOnly
                setFusion(mCurrentFusionType)
                // thermalSteeringView.visibility = View.GONE
                thermalRecyclerNight.setTwoLightSelected(TwoLightType.CORRECT, false)
            }
            TwoLightType.CORRECT -> {//Registration
                if (isSelected){
                    // thermalSteeringView.visibility = View.VISIBLE
                    if (mCurrentFusionType != DualCameraParams.FusionType.LPYFusion && mCurrentFusionType != DualCameraParams.FusionType.MeanFusion) {
                        mCurrentFusionType = DualCameraParams.FusionType.LPYFusion
                        thermalRecyclerNight.twoLightType = TwoLightType.TWO_LIGHT_1
                        SaveSettingUtil.fusionType = SaveSettingUtil.FusionTypeLPYFusion
                        setFusion(DualCameraParams.FusionType.LPYFusion)
                    }
                }else{
                    // thermalSteeringView.visibility = View.GONE
                }
            }
            else -> {
                super.setTwoLight(twoLightType, isSelected)
            }
        }
    }

    override fun getCameraViewBitmap(): Bitmap {
        if (imageEditBytes.size != dualView?.frameIrAndTempData?.size) {
            imageEditBytes = ByteArray(dualView!!.frameIrAndTempData.size)
        }
        System.arraycopy(dualView!!.frameIrAndTempData, 0, imageEditBytes, 0, imageEditBytes.size)
        return dualView?.scaledBitmap!!
    }


    override fun setTemperatureViewType() {
        temperatureView.productType = Const.TYPE_IR_DUAL
        cameraView.productType = Const.TYPE_IR_DUAL
    }

    override fun startUSB(isRestart: Boolean, isBadFrames: Boolean) {
        // Empty implementation for dual IR device
    }

    override fun setPColor(code: Int) {
        pseudoColorMode = code
        temperatureSeekbar.setPseudocode(pseudoColorMode)
        /**
         * SettingsPseudo-color【set pseudocolor】
         * [CN_TEXT]Core[CN_TEXT]([CN_TEXT]Pseudo-color[CN_TEXT],Settings[CN_TEXT])
         */
        // dualView?.dualUVCCamera?.setPseudocolor(PseudocodeUtils.changeDualPseudocodeModelByOld(pseudoColorMode))
        SaveSettingUtil.pseudoColorMode = pseudoColorMode
        thermalRecyclerNight.setPseudoColor(code)
    }

    override fun startISP() {
        setCustomPseudoColorList(
            customPseudoBean.getColorList(),
            customPseudoBean.getPlaceList(),
            customPseudoBean.isUseGray,
            customPseudoBean.maxTemp, customPseudoBean.minTemp
        )
    }

    override fun setCustomPseudoColorList(
        colorList: IntArray?,
        places: FloatArray?,
        isUseGray: Boolean,
        customMaxTemp: Float,
        customMinTemp: Float
    ) {
        irImageHelp.setColorList(colorList, places, isUseGray,customMaxTemp,customMinTemp)
    }

    override fun setRotate(rotateInt: Int) {
        super.setRotate(rotateInt)
        runOnUiThread {
            // thermalSteeringView.rotationIR = rotateInt
        }
        //Dual light[CN_TEXT]RotateAngle[CN_TEXT]
        when (rotateInt) {
            0 -> dualView?.dualUVCCamera?.setImageRotate(DualCameraParams.TypeLoadParameters.ROTATE_90)
            90 -> dualView?.dualUVCCamera?.setImageRotate(DualCameraParams.TypeLoadParameters.ROTATE_180)
            180 -> dualView?.dualUVCCamera?.setImageRotate(DualCameraParams.TypeLoadParameters.ROTATE_270)
            270 -> dualView?.dualUVCCamera?.setImageRotate(DualCameraParams.TypeLoadParameters.ROTATE_0)
        }
    }

    override fun onIrFrame(irFrame: ByteArray?): ByteArray {
        System.arraycopy(irFrame, 0, preIrData, 0, preIrData.size)
        System.arraycopy(irFrame, preIrData.size, preTempData, 0, preTempData.size)
        if (irImageHelp.getColorList() != null){
            //[CN_TEXT]Pseudo-color[CN_TEXT]
            LibIRProcess.convertYuyvMapToARGBPseudocolor(
                preIrData, (Const.IR_WIDTH * Const.IR_HEIGHT).toLong(),
                CommonParams.PseudoColorType.PSEUDO_1, preIrARGBData
            )
        }else{
            LibIRProcess.convertYuyvMapToARGBPseudocolor(
                preIrData, (Const.IR_WIDTH * Const.IR_HEIGHT).toLong(),
                PseudocodeUtils.changePseudocodeModeByOld(pseudoColorMode), preIrARGBData
            )
        }
        irImageHelp.customPseudoColor(preIrARGBData,preTempData,Const.IR_WIDTH,Const.IR_HEIGHT)
        //[CN_TEXT],[CN_TEXT]Pseudo-color[CN_TEXT]
        irImageHelp.setPseudoColorMaxMin(
            preIrARGBData, preTempData, editMaxValue,
            editMinValue, Const.IR_WIDTH,Const.IR_HEIGHT)
        //[CN_TEXT]，Dual light[CN_TEXT]Rotate[CN_TEXT]，[CN_TEXT]，（[CN_TEXT]256*192）
       val tempData =irImageHelp.contourDetection(alarmBean,
           preIrARGBData,preTempData,
            Const.IR_HEIGHT,Const.IR_WIDTH)
        System.arraycopy(tempData,0, preIrARGBData, 0, preIrARGBData.size)
        return preIrARGBData
    }

    override fun irStop() {
        try {
            configJob?.cancel()
            // timeDownView?.cancel()  // View doesn't exist in current layout
            if (isVideo) {
                isVideo = false
                videoRecord?.stopRecord()
                videoTimeClose()
                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    EventBus.getDefault().post(GalleryAddEvent())
                }
                lifecycleScope.launch {
                    delay(500)
                    thermalRecyclerNight.refreshImg()
                }
            }
        } catch (_: Exception) {
        }finally {
            ircmd?.onDestroy()
            ircmd = null
        }
    }

    /**
     * [CN_TEXT]
     */
    override fun initVideoRecordFFmpeg() {
        videoRecord = VideoRecordFFmpeg(
            cameraView,
            cameraPreview,
            temperatureView,
            curChooseTabPos == 1,
            cl_seek_bar,
            temp_bg,
            compassView, dualView,
            carView = layCarDetectPrompt
        )
    }

    override fun irStart() {
        if (!isrun) {
            tvTypeInd.isVisible = false
            startUSB(false,false)
            startISP()
            isrun = true
            //[CN_TEXT]
            configParam()
            thermalRecyclerNight.updateCameraModel()
            initIRConfig()
        }
    }
    override fun setDispViewData(dualDisp: Int) {
        // thermalSteeringView.moveX = dualDisp
    }
    override fun autoConfig() {
        lifecycleScope.launch(Dispatchers.IO) {
            dualView?.let {
                if (!it.auto_gain_switch) {
                    switchAutoGain(true)
                    ToastTools.showShort(R.string.auto_open)
                }
                gainSelChar = CameraItemBean.TYPE_TMP_ZD
            }
        }
        dismissCameraLoading()
        thermalRecyclerNight.setTempLevel(CameraItemBean.TYPE_TMP_ZD)
    }
    override fun switchAutoGain(boolean: Boolean) {
        dualView?.auto_gain_switch = boolean
    }

}
