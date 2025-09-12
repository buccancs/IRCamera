package com.topdon.lib.core.repository

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

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
