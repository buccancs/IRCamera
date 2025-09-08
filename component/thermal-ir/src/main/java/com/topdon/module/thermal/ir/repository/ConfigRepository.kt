package com.topdon.module.thermal.ir.repository

import com.google.gson.Gson
import com.topdon.lib.core.common.SharedManager
import com.topdon.module.thermal.ir.bean.DataBean
import com.topdon.module.thermal.ir.bean.ModelBean
import java.lang.Exception

object ConfigRepository {

    fun read(isTC007: Boolean): ModelBean = try {
        // Only TC001 is supported now, always use the standard IR config
        Gson().fromJson(SharedManager.getIRConfig(), ModelBean::class.java)
    } catch (_: Exception) {
        //SP
        ModelBean(DataBean(id = 0, use = true))
    }

    fun update(isTC007: Boolean, bean: ModelBean) {
        // Only TC001 is supported now, always use the standard IR config
        SharedManager.setIRConfig(Gson().toJson(bean))
    }

    fun readConfig(isTC007: Boolean): DataBean {
        val config = read(isTC007)
        if (config.defaultModel.use) {
            return config.defaultModel
        }
        config.myselfModel.forEach {
            if (it.use) {
                return it
            }
        }
        return config.defaultModel
    }

}