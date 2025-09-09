package com.topdon.lib.core.http.ts004
import com.topdon.lms.sdk.xutils.common.Callback
import com.topdon.lms.sdk.xutils.http.RequestParams
import com.topdon.lms.sdk.xutils.x

object HttpUtils {
    /**
     * SettingsPseudo-color[CN_TEXT]
     * @param mode              Pseudo-color[CN_TEXT]
     * @param iResponseCallback [CN_TEXT]
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
     * [CN_TEXT]Pseudo-color[CN_TEXT]
     */
    fun getPseudoColor(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_PSEUDO_COLOR
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * Settings[CN_TEXT]
     * @param mode              [CN_TEXT]:[CN_TEXT]0-100
     * @param iResponseCallback [CN_TEXT]
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
     * [CN_TEXT]
     */
    fun getBrightness(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_PANEL_PARAM
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * SettingsPicture in picture
     * @param iResponseCallback [CN_TEXT]
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
     * [CN_TEXT]Picture in picture
     */
    fun getPip(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_PIP
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * Settings[CN_TEXT]
     * @param factor            [CN_TEXT]:1,2,4,8
     * @param iResponseCallback [CN_TEXT]
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
     * [CN_TEXT]
     */
    fun getZoom(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_ZOOM
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * SettingsPhoto
     * @param iResponseCallback [CN_TEXT]
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
     * @param enable [CN_TEXT]
     * @param iResponseCallback [CN_TEXT]
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
     * [CN_TEXT]VideoState
     * @param iResponseCallback [CN_TEXT]
     * @void
     */
    fun getVideoStatus(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_RECORD_STATUS
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * [CN_TEXT]
     */
    fun getVersion(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_VERSION
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * [CN_TEXT]
     */
    fun getDeviceDetails(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_DEVICE_DETAILS
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * [CN_TEXT]
     */
    fun getFreeSpace(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_FREE_SPACE
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }

    /**
     * [CN_TEXT]Settings
     */
    fun getResetAll(iResponseCallback: Callback.CommonCallback<String>?) {
        val params = RequestParams()
        params.uri = TS004URL.GET_RESET_ALL
        params.isAsJsonContent = true
        x.http().post(params, iResponseCallback!!)
    }
}
