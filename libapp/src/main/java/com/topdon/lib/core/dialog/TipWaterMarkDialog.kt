package com.topdon.lib.core.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.topdon.lib.core.R
import com.topdon.lib.core.bean.WatermarkBean
import com.topdon.lib.core.*
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.databinding.DialogTipWatermarkBinding
import com.topdon.lib.core.utils.CommUtils
import com.topdon.lib.core.utils.ScreenUtil
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import java.util.*

/**
 * 2D-编辑 水印
 */
class TipWaterMarkDialog : Dialog {
    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    class Builder(val context: Context, private val watermarkBean: WatermarkBean) {
        var dialog: TipWaterMarkDialog? = null
        private var closeEvent: ((WatermarkBean) -> Unit)? = null
        private var canceled = false

        private lateinit var binding: DialogTipWatermarkBinding
        private var locationManager: LocationManager? = null
        private var locationProvider: String? = null

        fun setCancelListener(event: ((WatermarkBean) -> Unit)? = null): Builder {
            this.closeEvent = event
            return this
        }

        fun setCanceled(canceled: Boolean): Builder {
            this.canceled = canceled
            return this
        }

        fun dismiss() {
            this.dialog!!.dismiss()
        }


        fun create(): TipWaterMarkDialog {
            if (dialog == null) {
                dialog = TipWaterMarkDialog(context!!, R.style.InfoDialog)
            }
            val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            binding = DialogTipWatermarkBinding.inflate(inflater)
            val view = binding.root
            
            updateWaterMark(false)

            binding.switchWatermark.setOnCheckedChangeListener { _, isChecked ->
                updateWaterMark(isChecked)
            }
            binding.switchDateTime.setOnCheckedChangeListener { _, _ ->

            }
            binding.tvIKnow.setOnClickListener {
                dismiss()
                closeEvent?.invoke(
                    WatermarkBean(
                        binding.switchWatermark.isChecked,
                        binding.edTitle.text.toString(),
                        binding.edAddress.text.toString(),
                        binding.switchDateTime.isChecked,
                    )
                )
            }
            binding.imgLocation.setOnClickListener {
                checkLocationPermission()
            }
            binding.switchWatermark.isChecked = watermarkBean.isOpen
            binding.switchDateTime.isChecked = watermarkBean.isAddTime
            binding.edTitle.setText(watermarkBean.title.ifEmpty { SharedManager.watermarkBean.title })
            binding.edAddress.setText(watermarkBean.address)


            dialog!!.addContentView(
                view,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            )
            val lp = dialog!!.window!!.attributes
            val wRatio =
                if (context!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //竖屏
                    0.85
                } else {
                    //横屏
                    0.35
                }
            lp.width = (ScreenUtil.getScreenWidth(context) * wRatio).toInt() //设置宽度
            dialog!!.window!!.attributes = lp

            dialog!!.setCanceledOnTouchOutside(canceled)
            binding.imgClose.setOnClickListener {
                dismiss()
//              closeEvent?.invoke(
//                    WatermarkBean(
//                        binding.switchWatermark.isChecked,
//                        binding.edTitle.text.toString(),
//                        binding.edAddress.text.toString(),
//                        binding.switchDateTime.isChecked,
//                    )
//                )
            }
            dialog!!.setContentView(view)
            return dialog as TipWaterMarkDialog
        }

        private fun checkLocationPermission() {
            if (!XXPermissions.isGranted(
                    context, listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            ) {
                if (BaseApplication.instance.isDomestic()) {
                    TipDialog.Builder(context)
                        .setMessage(context.getString(R.string.permission_request_location_app,
                            CommUtils.getAppName()))
                        .setCancelListener(R.string.app_cancel)
                        .setPositiveListener(R.string.app_confirm) {
                            initLocationPermission()
                        }
                        .create().show()
                } else {
                    initLocationPermission()
                }
            } else {
                initLocationPermission()
            }
        }

        private fun initLocationPermission() {
            //定位
            XXPermissions.with(context)
                .permission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                        if (all){
                            var addressText: String? = getLocation()
                            if (addressText == null){
                                ToastUtils.showShort(R.string.get_Location_failed)
                            }else{
                                binding.edAddress.setText(addressText)
                            }
                        }else{
                            ToastUtils.showShort(R.string.scan_ble_tip_authorize)
                        }
                    }
                    override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                        if (never) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            if (BaseApplication.instance.isDomestic()){
                                ToastUtils.showShort(R.string.app_location_content)
                                return
                            }
                            TipDialog.Builder(context)
                                .setTitleMessage(context!!.getString(R.string.app_tip))
                                .setMessage(context!!.getString(R.string.app_location_content))
                                .setPositiveListener(R.string.app_open){
                                    XXPermissions.startPermissionActivity(context, permissions);
                                }
                                .setCancelListener(R.string.app_cancel){
                                }
                                .setCanceled(true)
                                .create()
                                .show()
                        } else {
                            ToastUtils.showShort(R.string.scan_ble_tip_authorize)
                        }
                    }

                })
        }

        private fun updateWaterMark(isCheck: Boolean){
            if(isCheck){
                binding.llWatermarkContent.alpha = 1f
                binding.llWatermarkContent.isEnabled = true
                binding.switchDateTime.isEnabled = true
                binding.edTitle.isEnabled = true
                binding.edAddress.isEnabled = true
                binding.imgLocation.isEnabled = true
            }else{
                binding.llWatermarkContent.alpha = 0.5f
                binding.llWatermarkContent.isEnabled = false
                binding.switchDateTime.isEnabled = false
                binding.edTitle.isEnabled = false
                binding.edAddress.isEnabled = false
                binding.imgLocation.isEnabled = false
            }
        }

        @SuppressLint("MissingPermission")
        private fun getLocation() : String? {
            //1.获取位置管理器
            locationManager = context!!.getSystemService(RxAppCompatActivity.LOCATION_SERVICE) as LocationManager

            //2.获取位置提供器，GPS或是NetWork
            val providers = locationManager?.getProviders(true)
            locationProvider = if (providers!!.contains(LocationManager.GPS_PROVIDER)) {
                //如果是GPS
                LocationManager.GPS_PROVIDER
            } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                //如果是Network
                LocationManager.NETWORK_PROVIDER
            } else {
                return null
            }
            var location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location == null){
                location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            return if (location == null){
                null
            }else{
                getAddress(location)
            }
        }

        //获取地址信息:城市、街道等信息
        private fun getAddress(location: Location?): String {
            var result: List<Address?>? = null
            try {
                if (location != null) {
                    val gc = Geocoder(context!!, Locale.getDefault())
                    result = gc.getFromLocation(
                        location.latitude,
                        location.longitude, 1
                    )
                    Log.v("TAG", "获取地址信息：$result")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            var str = ""
            if (result!=null && result.isNotEmpty()){
                result?.get(0)?.let {
                    str +=  getNullString(it.adminArea)
                    if (TextUtils.isEmpty(it.subLocality) && !str.contains(getNullString(it.subAdminArea))){
                        str +=  getNullString(it.subAdminArea)
                    }
                    if (!str.contains(getNullString(it.locality))){
                        str +=  getNullString(it.locality)
                    }
                    if (!str.contains(getNullString(it.subLocality))){
                        str +=  getNullString(it.subLocality)
                    }
                }
            }
            return str
        }

        private fun getNullString(str : String?):String{
            return if (str.isNullOrEmpty()){
                ""
            }else{
                str
            }
        }

    }
}