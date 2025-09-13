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
     * forcedUpgradeFlag: 1 强制update    0 非强制update
     * descType: 包含3时,显示给用户(descType获取升级描述信息)
     */
    fun checkVersion() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                if (TimeUtils.isToday(SharedManager.getVersionCheckDate())) {
//                    Log.w("123", "今天已有版本update提示")
//                    return@launch
//                }
//                val result: CheckVersionJson = LmsRepository.getVersionInfo() ?: return@launch
//                /*if (result.googleVerCode > AppUtils.getAppVersionCode()) {
//                    // google play需要升级
//                    updateTip(result)
//                    return@launch
//                }*/
//                if (VersionTool.checkVersion(remoteStr = result.versionNo ?: "1.0", localStr = AppUtils.getAppVersionName())) {
//                    // google play检测不出时,官方升级,根据app情况跳转对应的升级渠道
//                    updateTip(result)
//                    return@launch
//                }
//            } catch (e: Exception) {
//                XLog.e("检测异常: ${e.message}")
//            }
//        }
    }

    private fun processVersionResult(result: CheckVersionJson) {
        val isForcedUpgrade = result.isForcedUpgrade == 1
        val description = getDescription(result.softConfigOtherTypeVOList)
        val downPageUrl = result.downloadPageUrl
        val sizeStr = "${result.notUnZipSize}MB"

        XLog.i("view,view: $description, view: $isForcedUpgrade")

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
     * view
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
