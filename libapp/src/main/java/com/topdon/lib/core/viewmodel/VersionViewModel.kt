package com.topdon.lib.core.viewmodel

import com.elvishew.xlog.XLog
import com.topdon.lib.core.bean.event.VersionUpData
import com.topdon.lib.core.bean.json.CheckVersionJson
import com.topdon.lib.core.bean.json.SoftConfigOtherTypeVO
import com.topdon.lib.core.ktbase.BaseViewModel
import com.topdon.lib.core.utils.SingleLiveEvent

class VersionViewModel : BaseViewModel() {
    val updateLiveData = SingleLiveEvent<VersionUpData>()

    /**
     * forcedUpgradeFlag: 1 [CN_TEXT]    0 [CN_TEXT]
     * descType: [CN_TEXT]3[CN_TEXT],[CN_TEXT](descType[CN_TEXT])
     */
    fun checkVersion() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                if (TimeUtils.isToday(SharedManager.getVersionCheckDate())) {
//                    Log.w("123", "[CN_TEXT]")
//                    return@launch
//                }
//                val result: CheckVersionJson = LmsRepository.getVersionInfo() ?: return@launch
//                /*if (result.googleVerCode > AppUtils.getAppVersionCode()) {
//                    // google play[CN_TEXT]
//                    updateTip(result)
//                    return@launch
//                }*/
//                if (VersionTool.checkVersion(remoteStr = result.versionNo ?: "1.0", localStr = AppUtils.getAppVersionName())) {
//                    // google play[CN_TEXT],[CN_TEXT],[CN_TEXT]app[CN_TEXT]
//                    updateTip(result)
//                    return@launch
//                }
//            } catch (e: Exception) {
//                XLog.e("[CN_TEXT]: ${e.message}")
//            }
//        }
    }

    private fun updateTip(result: CheckVersionJson) {
        val isForcedUpgrade = (result.forcedUpgradeFlag?.toInt() ?: 0) == 1 // 1: [CN_TEXT]
        val description = getDescription(result.softConfigOtherTypeVOList)
        val downPageUrl = result.downloadPageUrl
        val sizeStr = "${result.notUnZipSize}MB"

        XLog.i("[CN_TEXT],[CN_TEXT]: $description, [CN_TEXT]: $isForcedUpgrade")

        val versionUpData =
            VersionUpData(
                versionNo = result.versionNo ?: "",
                isForcedUpgrade = isForcedUpgrade,
                description = description,
                downPageUrl = downPageUrl,
                sizeStr = sizeStr,
            )
        updateLiveData.postValue(versionUpData)
    }

    /**
     * [CN_TEXT]
     */
    private fun getDescription(list: List<SoftConfigOtherTypeVO>?): String {
        list?.forEach {
            if (it.descType == 3) {
                return it.textDescription
            }
        }
        return ""
    }
}
