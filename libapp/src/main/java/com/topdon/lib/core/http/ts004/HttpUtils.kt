package com.topdon.lib.core.http.ts004
import com.topdon.lms.sdk.xutils.common.Callback
import com.topdon.lms.sdk.xutils.http.RequestParams
import com.topdon.lms.sdk.xutils.x

object HttpUtils {
    /**
     * SettingsPseudo-colorutility
     * @param mode              Pseudo-colorutility
     * @param iResponseCallback utility
     * @ void
     */
    fun setPseudoColor(
        mode: Int,
        iResponseCallback: Callback.CommonCallback<String>?,
    ) {
        val params = RequestParams()
        params.addBodyParameter("enable", false)
        params.addBodyParameter("mode", mode)
        params.uri = TS004URL.SET_PSEUDO_COLOR
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * utilityPseudo-colorutility
     */
    fun getPseudoColor(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_PSEUDO_COLOR
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * Settingsutility
     * @param mode              utility:utility0-100
     * @param iResponseCallback utility
     * @ void
     */
    fun setBrightness(
        brightness: Int,
        iResponseCallback: Callback.CommonCallback<String>?,
    ) {
        val params = RequestParams()
        params.addBodyParameter("brightness", brightness)
        params.uri = TS004URL.SET_PANEL_PARAM
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * utility
     */
    fun getBrightness(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_PANEL_PARAM
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * SettingsPicture in picture
     * @param iResponseCallback utility
     * @ void
     */
    fun setPip(
        enable: Boolean,
        iResponseCallback: Callback.CommonCallback<String>?,
    ) {
        val params = RequestParams()
        params.addBodyParameter("enable", enable)
        params.uri = TS004URL.SET_PIP
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * utilityPicture in picture
     */
    fun getPip(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_PIP
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * Settingsutility
     * @param factor            utility:1,2,4,8
     * @param iResponseCallback utility
     * @ void
     */
    fun setZoom(
        factor: Int,
        iResponseCallback: Callback.CommonCallback<String>?,
    ) {
        val params = RequestParams()
        params.addBodyParameter("enable", true)
        params.addBodyParameter("factor", factor)
        params.uri = TS004URL.SET_ZOOM
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * utility
     */
    fun getZoom(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_ZOOM
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * SettingsPhoto
     * @param iResponseCallback utility
     * @void
     */
    fun setCamera(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.SET_SNAPSHOT
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * SettingsVideo
     * @param enable utility
     * @param iResponseCallback utility
     * @void
     */
    fun setVideo(
        enable: Boolean,
        iResponseCallback: Callback.CommonCallback<String>?,
    ) {
        val params = RequestParams()
        params.addBodyParameter("enable", enable)
        params.uri = TS004URL.GET_VRECORD
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * utilityVideoState
     * @param iResponseCallback utility
     * @void
     */
    fun getVideoStatus(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_RECORD_STATUS
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * utility
     */
    fun getVersion(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_VERSION
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * utility
     */
    fun getDeviceDetails(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_DEVICE_DETAILS
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * utility
     */
    fun getFreeSpace(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_FREE_SPACE
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * utilitySettings
     */
    fun getResetAll(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_RESET_ALL
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }
}
