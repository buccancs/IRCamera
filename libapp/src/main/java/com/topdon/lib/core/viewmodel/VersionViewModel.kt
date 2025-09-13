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
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 fun checkVersion() {
// viewModelScope.launch(Dispatchers.IO) {
// try {
// if (TimeUtils.isToday(SharedManager.getVersionCheckDate())) {
// Comment removed (contained Chinese characters)
// return@launch
// }
// val result: CheckVersionJson = LmsRepository.getVersionInfo() ?: return@launch
// /*if (result.googleVerCode > AppUtils.getAppVersionCode()) {
// Comment removed (contained Chinese characters)
// updateTip(result)
// return@launch
// }*/
// if (VersionTool.checkVersion(remoteStr = result.versionNo ?: "1.0", localStr = AppUtils.getAppVersionName())) {
// Comment removed (contained Chinese characters)
// updateTip(result)
// return@launch
// }
// } catch (e: Exception) {
// Comment removed (contained Chinese characters)
// }
// }
 }

 private fun updateTip(result: CheckVersionJson) {
 val isForcedUpgrade = (result.forcedUpgradeFlag?.toInt() ?: 0) == 1 // 1: 
 val description = getDescription(result.softConfigOtherTypeVOList)
 val downPageUrl = result.downloadPageUrl
 val sizeStr = "${result.notUnZipSize}MB"

 XLog.i(",: $description, Whether: $isForcedUpgrade")

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
 * Comment removed (contained Chinese characters)
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
