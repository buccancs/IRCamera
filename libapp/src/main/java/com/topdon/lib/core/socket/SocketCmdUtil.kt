package com.topdon.lib.core.socket

import android.text.TextUtils
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

object SocketCmdUtil {
    /**
     * Function description.
     */
    fun getSocketCmd(cmd: Int): String?{
        var cmdJson: String? = null
        try {
            val gson = Gson()
            val paramMap: HashMap<String, Int> = HashMap()
            paramMap["cmd"] = cmd
            cmdJson = gson.toJson(paramMap)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return cmdJson
        }
    }

    /**
     * Function description.
     */
    fun getCmdResponse(response: String?): Int? {
        var cmd: Int? = null
        if (TextUtils.isEmpty(response)) return null
        try {
            val jsonObject = JSONObject(response)
            cmd = jsonObject.getInt("cmd")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return cmd
    }

    /**
     * Function description.
     */
    fun getIpResponse(response: String?): String? {
        var ip: String? = null
        if (TextUtils.isEmpty(response)) return null
        try {
            val jsonObject = JSONObject(response)
            ip = jsonObject.getString("ip")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return ip
    }
}