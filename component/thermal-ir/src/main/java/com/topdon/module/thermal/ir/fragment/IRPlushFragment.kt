package com.topdon.module.thermal.ir.fragment

import android.graphics.Bitmap
import android.view.SurfaceView
import android.view.View
import com.infisense.usbdual.Const
import com.infisense.usbdual.camera.DualViewWithExternalCameraCommonApi
import com.infisense.usbir.view.TemperatureView
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.activity.BaseIRPlushFragment
import com.topdon.module.thermal.ir.databinding.FragmentIrPlushBinding

/**
 * des:
 * author: CaiSongL
 * date: 2024/9/3 11:43
 **/
class IRPlushFragment : BaseIRPlushFragment() {

    private var _binding: FragmentIrPlushBinding? = null
    private val binding get() = _binding!!

    override fun getSurfaceView(): SurfaceView {
        return binding.dualTextureViewNativeCamera
    }

    override fun getTemperatureDualView(): TemperatureView {
        return binding.temperatureView
    }

    override suspend fun onDualViewCreate(dualView: DualViewWithExternalCameraCommonApi?) {

    }

    override fun isDualIR(): Boolean {
        return true
    }

    override fun setTemperatureViewType() {
        getTemperatureDualView().productType = Const.TYPE_IR_DUAL
    }

    override fun initContentView(): Int {
        _binding = FragmentIrPlushBinding.inflate(layoutInflater)
        return R.layout.fragment_ir_plush
    }

    override fun initData() {
    }

    override fun initView() {
        super.initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun getBitmap() : Bitmap?{
        return dualView?.scaledBitmap
    }
}