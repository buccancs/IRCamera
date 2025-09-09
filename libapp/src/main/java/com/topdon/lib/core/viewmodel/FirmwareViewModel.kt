package com.topdon.lib.core.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.Utils
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.topdon.lib.core.R
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.repository.ProductBean
import com.topdon.lib.core.repository.TC007Repository
import com.topdon.lib.core.repository.TS004Repository
import com.topdon.lms.sdk.LMS
import com.topdon.lms.sdk.UrlConstant
import com.topdon.lms.sdk.bean.CommonBean
import com.topdon.lms.sdk.network.HttpProxy
import com.topdon.lms.sdk.network.IResponseCallback
import com.topdon.lms.sdk.network.ResponseBean
import com.topdon.lms.sdk.utils.DateUtils
import com.topdon.lms.sdk.utils.LanguageUtil
import com.topdon.lms.sdk.xutils.http.RequestParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.NumberFormatException
import java.util.TimeZone
import java.util.concurrent.CountDownLatch

/**
 * [CN_TEXT]
 */
class FirmwareViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        /**
         * TS004 [CN_TEXT] [CN_TEXT].
         */
        private const val TS004_SOFT_CODE = "TS004_FirmwareSW_Scope"

        /**
         * TC007 [CN_TEXT] [CN_TEXT].
         */
        private const val TC007_SOFT_CODE = "TC007_FirmwareSW_Wireless"

        /**
         * TS004 apk [CN_TEXT].
         */
        private const val TS004_FIRMWARE_VERSION = "V1.70"

        /**
         * TS004 apk [CN_TEXT].
         */
        private const val TS004_FIRMWARE_NAME = "TS004V1.70.zip"

        /**
         * TC007 apk [CN_TEXT].
         */
        private const val TC007_FIRMWARE_VERSION = "V4.06"

        /**
         * TC007 apk [CN_TEXT].
         */
        private const val TC007_FIRMWARE_NAME = "TC007V4.06.zip"

        private const val USE_DEBUG_SN = false
        private const val TS004_DEBUG_SN = "1D003655A10016"
        private const val TS004_DEBUG_RANDOM_NUM = "8D2N01"
        private const val TC007_DEBUG_SN = "1D004714E10002"
        private const val TC007_DEBUG_RANDOM_NUM = "EN6L6Q"
    }

    /**
     * [CN_TEXT]State，[CN_TEXT].
     */
    @Volatile
    private var isRequest = false

    /**
     * [CN_TEXT] LiveData.
     * null[CN_TEXT]
     */
    val firmwareDataLD: MutableLiveData<FirmwareData?> = MutableLiveData()

    /**
     * [CN_TEXT] LiveData.
     * true-[CN_TEXT] false-[CN_TEXT]
     */
    val failLD: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * [CN_TEXT].
     * @param version [CN_TEXT]，V1.00[CN_TEXT]
     * @param updateStr [CN_TEXT]
     * @param downUrl [CN_TEXT] URL
     * @param size [CN_TEXT]，[CN_TEXT] byte
     */
    data class FirmwareData(
        val version: String,
        val updateStr: String,
        val downUrl: String,
        val size: Long,
    )

    /**
     * [CN_TEXT]，[CN_TEXT]：
     * - [firmwareDataLD] ([CN_TEXT])
     * - [failLD] ([CN_TEXT])
     * @param isTS004 true-TS004 false-TC007
     */
    fun queryFirmware(isTS004: Boolean) {
        if (isRequest) { // [CN_TEXT]，[CN_TEXT]
            return
        }
        isRequest = true

        viewModelScope.launch(Dispatchers.IO) {
            // [CN_TEXT]，V3.30[CN_TEXT] apk [CN_TEXT]，[CN_TEXT]
            /*if (isTS004) {
                //[CN_TEXT] TS004 [CN_TEXT] SN、[CN_TEXT]
                val deviceInfo: DeviceInfo? = TS004Repository.getDeviceInfo()?.data
                if (deviceInfo == null) {
                    XLog.w("TS004 [CN_TEXT] - [CN_TEXT] SN、[CN_TEXT] [CN_TEXT]!")
                    failLD.postValue(false)
                    isRequest = false
                    return@launch
                }

                //[CN_TEXT] TS004 [CN_TEXT]
                val firmware: String? = TS004Repository.getVersion()?.data?.firmware
                if (firmware == null) {
                    XLog.w("TS004 [CN_TEXT] - [CN_TEXT] [CN_TEXT] [CN_TEXT]!")
                    failLD.postValue(false)
                    isRequest = false
                    return@launch
                }

                val sn: String = if (USE_DEBUG_SN) TS004_DEBUG_SN else deviceInfo.sn
                val randomNum: String = if (USE_DEBUG_SN) TS004_DEBUG_RANDOM_NUM else deviceInfo.code
                getInfoFromNetwork(true, sn, randomNum, firmware)
            } else {
                //[CN_TEXT] TC007 [CN_TEXT] SN、[CN_TEXT]
                val productInfo: ProductBean? = TC007Repository.getProductInfo()
                if (productInfo == null) {
                    XLog.w("TC007 [CN_TEXT] - [CN_TEXT] SN、[CN_TEXT] [CN_TEXT]!")
                    failLD.postValue(false)
                    isRequest = false
                    return@launch
                }

                val sn: String = if (USE_DEBUG_SN) TC007_DEBUG_SN else productInfo.ProductSN
                val randomNum: String = if (USE_DEBUG_SN) TC007_DEBUG_RANDOM_NUM else productInfo.Code
                val firmware = "V${productInfo.getVersionStr()}"
                getInfoFromNetwork(false, sn, randomNum, firmware)
            }*/

            // [CN_TEXT]，V3.30[CN_TEXT] apk [CN_TEXT]，[CN_TEXT]
            if (isTS004) {
                // [CN_TEXT] TS004 [CN_TEXT]
                val firmware: String? = TS004Repository.getVersion()?.data?.firmware
                if (firmware == null) {
                    XLog.w("TS004 [CN_TEXT] - [CN_TEXT] [CN_TEXT] [CN_TEXT]!")
                    failLD.postValue(false)
                    isRequest = false
                    return@launch
                }

                getInfoFromAssets(true, firmware)
            } else {
                // [CN_TEXT] TC007 [CN_TEXT]
                val productInfo: ProductBean? = TC007Repository.getProductInfo()
                if (productInfo == null) {
                    XLog.w("TC007 [CN_TEXT] - [CN_TEXT] SN、[CN_TEXT] [CN_TEXT]!")
                    failLD.postValue(false)
                    isRequest = false
                    return@launch
                }

                getInfoFromAssets(false, "V${productInfo.getVersionStr()}")
            }
        }
    }

    /**
     * [CN_TEXT] assets [CN_TEXT]，[CN_TEXT] post [CN_TEXT] LiveData
     */
    private fun getInfoFromAssets(
        isTS004: Boolean,
        firmware: String,
    ) {
        val apkVersionStr = if (isTS004) TS004_FIRMWARE_VERSION else TC007_FIRMWARE_VERSION
        val apkFirmwareName = if (isTS004) TS004_FIRMWARE_NAME else TC007_FIRMWARE_NAME

        val newVersion: Double = getVersionFromStr(apkVersionStr)
        val currentVersion: Double = getVersionFromStr(firmware)
        XLog.d("${if (isTS004) "TS004" else "TC007"} [CN_TEXT] - Current[CN_TEXT]：$currentVersion apk[CN_TEXT]：$newVersion")
        if (newVersion <= currentVersion) { // Current[CN_TEXT]
            firmwareDataLD.postValue(null)
            isRequest = false
            return
        }

        val firmwareFile = FileConfig.getFirmwareFile(apkFirmwareName)
        try {
            val application: Application = getApplication()
            val inputStream = application.assets.open(apkFirmwareName)
            val outputStream: OutputStream = FileOutputStream(firmwareFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            XLog.e("${if (isTS004) "TS004" else "TC007"} [CN_TEXT] - [CN_TEXT]! ${e.message}")
            FileUtils.delete(firmwareFile)
            firmwareDataLD.postValue(null)
            isRequest = false
            return
        }

        // [CN_TEXT]，[CN_TEXT]。
        val tipsStr = getApplication<Application>().getString(R.string.fireware_update_tips)

        firmwareDataLD.postValue(FirmwareData(apkVersionStr, tipsStr, apkFirmwareName, firmwareFile.length()))
        isRequest = false
    }

    /**
     * [CN_TEXT].
     */
    private suspend fun getInfoFromNetwork(
        isTS004: Boolean,
        sn: String,
        randomNum: String,
        firmware: String,
    ) {
        // [CN_TEXT]
        val bindCode = bindDevice(sn, randomNum)
        if (bindCode != LMS.SUCCESS && bindCode != 15109) {
            XLog.w("${if (isTS004) "TS004" else "TC007"} [CN_TEXT] - [CN_TEXT]! sn: $sn")
            failLD.postValue(bindCode == 15162)
            isRequest = false
            return
        }

        // [CN_TEXT]
        val packageData: PackageData? = querySoftPackage(sn, if (isTS004) TS004_SOFT_CODE else TC007_SOFT_CODE)
        if (packageData == null) {
            XLog.w("${if (isTS004) "TS004" else "TC007"} [CN_TEXT] - [CN_TEXT]!")
            failLD.postValue(false)
            isRequest = false
            return
        }

        val record: PackageData.Record? = packageData.getFirstRecord()
        val newVersionStr: String? = record?.maxUpdateVersion
        if (record == null || newVersionStr == null) { // [CN_TEXT]，[CN_TEXT]Current[CN_TEXT]
            XLog.d("${if (isTS004) "TS004" else "TC007"} [CN_TEXT] - [CN_TEXT]，[CN_TEXT]Current[CN_TEXT]")
            firmwareDataLD.postValue(null)
            isRequest = false
            return
        }

        val newVersion: Double = getVersionFromStr(newVersionStr)
        val currentVersion: Double = getVersionFromStr(firmware)
        XLog.d("${if (isTS004) "TS004" else "TC007"} [CN_TEXT] - Current[CN_TEXT]：$currentVersion [CN_TEXT]：$newVersion")
        if (newVersion <= currentVersion) { // Current[CN_TEXT]
            firmwareDataLD.postValue(null)
            isRequest = false
            return
        }

        // [CN_TEXT]
        val downloadData = queryDownloadUrl(sn, record.maxUpdateVersionSoftId)
        if (downloadData?.responseCode == LMS.SUCCESS) {
            firmwareDataLD.postValue(
                FirmwareData(
                    newVersionStr,
                    record.getUpdateStr(),
                    downloadData.downUrl ?: "",
                    downloadData.size ?: 0,
                ),
            )
        } else {
            XLog.w("${if (isTS004) "TS004" else "TC007"} [CN_TEXT] - [CN_TEXT]!")
            failLD.postValue(downloadData?.responseCode == 60312)
        }
        isRequest = false
    }

    /**
     * [CN_TEXT] SN、[CN_TEXT]Current[CN_TEXT].
     */
    private suspend fun bindDevice(
        sn: String,
        randomNum: String,
    ): Int {
        return withContext(Dispatchers.IO) {
            var code = LMS.SUCCESS
            val countDownLatch = CountDownLatch(1)
            LMS.getInstance().bindDevice(sn, randomNum, "", "") {
                code = it.code
                countDownLatch.countDown()
            }
            countDownLatch.await()
            return@withContext code
        }
    }

    /**
     * [CN_TEXT]Specified SN [CN_TEXT]
     */
    private suspend fun querySoftPackage(
        sn: String,
        softCode: String,
    ): PackageData? =
        withContext(Dispatchers.IO) {
            var packageData: PackageData? = null
            val countDownLatch = CountDownLatch(1)

            val url = UrlConstant.BASE_URL + "api/v1/user/deviceSoftOut/page"
            val params = RequestParams()
            params.addBodyParameter("sn", sn)
            params.addBodyParameter("softCode", softCode)
            params.addBodyParameter("downloadLanguageId", LanguageUtil.getLanguageId(Utils.getApp()))
            params.addBodyParameter("downloadPlatformId", 2) // 1-IOS 2-APP 3-[CN_TEXT] 4-PC 5-[CN_TEXT] 6-[CN_TEXT]
            params.addBodyParameter(
                "queryTime",
                DateUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("GMT")),
            )
            HttpProxy.instant.post(
                url,
                params,
                object : IResponseCallback {
                    override fun onResponse(response: String?) {
                        try {
                            val commonBean: CommonBean = ResponseBean.convertCommonBean(response, null)
                            packageData = Gson().fromJson(commonBean.data, PackageData::class.java)
                        } catch (_: Exception) {
                        }
                        countDownLatch.countDown()
                    }

                    override fun onFail(exception: Exception?) {
                        countDownLatch.countDown()
                    }
                },
            )

            countDownLatch.await()
            return@withContext packageData
        }

    /**
     * [CN_TEXT]Specified SN Specified[CN_TEXT].
     */
    private suspend fun queryDownloadUrl(
        sn: String,
        businessId: Int,
    ): DownloadData? =
        withContext(Dispatchers.IO) {
            var result: DownloadData? = null
            val countDownLatch = CountDownLatch(1)
            val url = UrlConstant.BASE_URL + "api/v1/user/deviceSoftOut/getFileUrl"
            val params = RequestParams()
            params.addBodyParameter("sn", sn)
            params.addBodyParameter("businessId", businessId)
            params.addBodyParameter("businessType", 20) // [CN_TEXT]Type，20-[CN_TEXT]
            params.addBodyParameter("productType", 20) // 0-[CN_TEXT] 10-[CN_TEXT] 20-[CN_TEXT]
            params.addBodyParameter("isCheckPoint", 0) // 0-[CN_TEXT] 1-[CN_TEXT]（[CN_TEXT]，[CN_TEXT]）
            HttpProxy.instant.post(
                url,
                params,
                object : IResponseCallback {
                    override fun onResponse(response: String?) {
                        try {
                            val commonBean: CommonBean = ResponseBean.convertCommonBean(response, null)
                            if (commonBean.code == LMS.SUCCESS) {
                                result = Gson().fromJson(commonBean.data, DownloadData::class.java)
                                result?.responseCode = commonBean.code
                            } else {
                                result = DownloadData("", 0, commonBean.code)
                            }
                        } catch (_: Exception) {
                        }
                        countDownLatch.countDown()
                    }

                    override fun onFail(exception: Exception?) {
                        countDownLatch.countDown()
                    }
                },
            )
            countDownLatch.await()
            return@withContext result
        }

    private fun getVersionFromStr(versionStr: String): Double =
        try {
            if (versionStr[0] == 'V') {
                versionStr.substring(1, versionStr.length).toDouble()
            } else {
                versionStr.toDouble()
            }
        } catch (e: NumberFormatException) {
            0.0
        }

    /**
     * [CN_TEXT] [CN_TEXT] [CN_TEXT].
     */
    private class PackageData {
        var records: List<Record>? = null

        fun getFirstRecord(): Record? = if (records?.isNotEmpty() == true) records?.get(0) else null

        data class Record(
            var maxUpdateVersion: String?, // [CN_TEXT]，[CN_TEXT]"V1.32"
            var maxUpdateVersionSoftId: Int, // [CN_TEXT]URL
            var maxVersionDetailResVO: MaxVersionDetailResVO?,
        ) {
            fun getUpdateStr(): String {
                val otherExplain: List<OtherExplain>? = maxVersionDetailResVO?.otherExplain
                if (otherExplain != null) {
                    for (data in otherExplain) {
                        if (data.valueType == 3) {
                            return data.textDescription ?: ""
                        }
                    }
                }
                return ""
            }
        }

        data class MaxVersionDetailResVO(
            val otherExplain: List<OtherExplain>?,
        )

        data class OtherExplain(
            val valueType: Int, // 1-[CN_TEXT] 2-[CN_TEXT] 3-[CN_TEXT] 4-Note[CN_TEXT]
            val textDescription: String?,
        )
    }

    /**
     * [CN_TEXT] [CN_TEXT] [CN_TEXT].
     */
    private data class DownloadData(
        val downUrl: String?,
        val size: Long?,
        var responseCode: Int,
    )
}
