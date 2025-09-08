package com.topdon.lib.core.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.TimeUtils
import com.elvishew.xlog.XLog
import com.topdon.lib.core.bean.event.VersionUpData
import com.topdon.lib.core.bean.json.CheckVersionJson
import com.topdon.lib.core.bean.json.SoftConfigOtherTypeVO
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.http.repository.LmsRepository
import com.topdon.lib.core.ktbase.BaseViewModel
import com.topdon.lib.core.tools.VersionTool
import com.topdon.lib.core.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VersionViewModel : BaseViewModel() {

    val updateLiveData = SingleLiveEvent<VersionUpData>()

     * forcedUpgradeFlag: 1     0
     * descType: 3,(descType)
    fun checkVersion() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                if (TimeUtils.isToday(SharedManager.getVersionCheckDate())) {
//                    Log.w(123, )
//                    return@launch
//                }
//                val result: CheckVersionJson = LmsRepository.getVersionInfo() ?: return@launch
//                /*if (result.googleVerCode > AppUtils.getAppVersionCode()) {
//                    // google play
//                    updateTip(result)
//                    return@launch
//                }*/
//                if (VersionTool.checkVersion(remoteStr = result.versionNo ?: 1.0, localStr = AppUtils.getAppVersionName())) {
//                    // google play,,app
//                    updateTip(result)
//                    return@launch
//                }
//            } catch (e: Exception) {
//                XLog.e(: ${e.message})
//            }
//        }
    }

    private fun updateTip(result: CheckVersionJson) {
        val isForcedUpgrade = (result.forcedUpgradeFlag?.toInt() ?: 0) == 1 //1:
        val description = getDescription(result.softConfigOtherTypeVOList)
        val downPageUrl = result.downloadPageUrl
        val sizeStr = "${result.notUnZipSize}MB"

        XLog.i(",: $description, : $isForcedUpgrade")

        val versionUpData = VersionUpData(
            versionNo = result.versionNo ?: "",
            isForcedUpgrade = isForcedUpgrade,
            description = description,
            downPageUrl = downPageUrl,
            sizeStr = sizeStr
        )
        updateLiveData.postValue(versionUpData)
    }

    private fun getDescription(list: List<SoftConfigOtherTypeVO>?): String {
        list?.forEach {
            if (it.descType == 3) {
                return it.textDescription
            }
        }
        return ""
    }

}