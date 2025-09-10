package com.topdon.lib.core.repository

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * Retrofit service interface for TS004 device API
 */
interface TS004Service {

    @POST("/api/v1/system/sendUpgradeFileStart")
    suspend fun sendUpgradeFileStart(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    @POST("/api/v1/system/sendUpgradeFileEnd")
    suspend fun sendUpgradeFileEnd(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    @POST("/api/v1/system/getFreeSpace")
    suspend fun freeSpace(): TS004Response<FreeSpaceBean>

    /**
     * data
     */
    @POST("/api/v1/system/formatStorage")
    suspend fun formatStorage(): TS004Response<Boolean>

    /**
     * dataSettings
     */
    @POST("/api/v1/system/resetAll")
    suspend fun resetAll(): TS004Response<Boolean>

    /**
     * Settingsdata
     */
    @POST("/api/v1/system/setTISR")
    suspend fun setTISR(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * data
     */
    @POST("/api/v1/system/getTISR")
    suspend fun getTISR(): TS004Response<TISRBean>
}
