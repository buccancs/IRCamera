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
 *
 * Created by LCG on 2024/2/27.
 */
interface TS004Service {
    /**
     * SettingsPseudo color[Chinese text]
     */
    @POST("/api/v1/system/setPseudoColor")
    suspend fun setPseudoColor(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * [Chinese text]Pseudo color[Chinese text]
     */
    @POST("/api/v1/system/getPseudoColor")
    suspend fun getPseudoColor(): TS004Response<PseudoColorBean>

    /**
     * Settings[Chinese text]
     */
    @POST("/api/v1/system/setRangeFind")
    suspend fun setRangeFind(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * [Chinese text]
     */
    @POST("/api/v1/system/getRangeFind")
    suspend fun getRangeFind(): TS004Response<RangeBean>

    /**
     * Settings[Chinese text]
     */
    @POST("/api/v1/system/setPanelParam")
    suspend fun setPanelParam(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * [Chinese text]
     */
    @POST("/api/v1/system/getPanelParam")
    suspend fun getPanelParam(): TS004Response<BrightnessBean>

    /**
     * Settings[Chinese text]in progress[Chinese text]
     */
    @POST("/api/v1/system/setPip")
    suspend fun setPip(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * [Chinese text]in progress[Chinese text]
     */
    @POST("/api/v1/system/getPip")
    suspend fun getPip(): TS004Response<PipBean>

    /**
     * Settings[Chinese text]
     */
    @POST("/api/v1/system/setZoom")
    suspend fun setZoom(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * [Chinese text]
     */
    @POST("/api/v1/system/getZoom")
    suspend fun getZoom(): TS004Response<ZoomBean>

    /**
     * SettingsPhoto capture
     */
    @POST("/api/v1/system/snapshot")
    suspend fun setSnapshot(): TS004Response<Boolean>

    /**
     * Settingsrecording
     */
    @POST("/api/v1/system/vrecord")
    suspend fun setVRecord(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * [Chinese text]recording[Chinese text]
     */
    @POST("/api/v1/system/getRecordStatus")
    suspend fun getVRecord(): TS004Response<RecordStatusBean>

    /**
     * [Chinese text].
     */
    @GET
    @Streaming
    suspend fun download(
        @Url url: String,
    ): ResponseBody

    /**
     * [Chinese text].
     */
    @POST("/api/v1/system/setDateTime")
    suspend fun syncTime(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * [Chinese text].
     */
    @POST("/api/v1/system/setTimeZone")
    suspend fun syncTimeZone(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * [Chinese text]message
     */
    @POST("/api/v1/system/getVersion")
    suspend fun getVersion(): TS004Response<VersionBean>

    /**
     * [Chinese text]message
     */
    @POST("/api/v1/system/getDeviceInfo")
    suspend fun getDeviceInfo(): TS004Response<DeviceInfo>

    /**
     * [Chinese text]
     */
    @POST("/api/v1/system/getFileCount")
    suspend fun getFileCount(
        @Body requestBody: RequestBody,
    ): TS004Response<FileCountBean>

    /**
     * [Chinese text]
     */
    @POST("/api/v1/system/getFileList")
    suspend fun getFileList(
        @Body requestBody: RequestBody,
    ): TS004Response<FilePageBean>

    /**
     * [Chinese text] id [Chinese text]
     */
    @POST("/api/v1/system/deleteFile")
    suspend fun deleteFile(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * [Chinese text]
     */
    @POST("/api/v1/system/remoteUpgrade")
    suspend fun firmwareUpdateStart(): TS004Response<Boolean>

    /**
     * [Chinese text]-start
     */
    @POST("/api/v1/system/sendUpgradeFileStart")
    suspend fun sendUpgradeFileStart(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * [Chinese text]-[Chinese text]
     */
    @Headers("Content-type: application/octet-stream")
    @POST("/api/v1/system/sendUpgradeFileData")
    suspend fun sendUpgradeFile(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * [Chinese text]-[Chinese text]
     */
    @POST("/api/v1/system/sendUpgradeFileEnd")
    suspend fun sendUpgradeFileEnd(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * [Chinese text].
     */
    @POST("/api/v1/system/getUpgradeStatus")
    suspend fun getUpgradeStatus(): TS004Response<UpgradeStatus>

    /**
     * [Chinese text]message
     */
    @POST("/api/v1/system/getFreeSpace")
    suspend fun freeSpace(): TS004Response<FreeSpaceBean>

    /**
     * [Chinese text]
     */
    @POST("/api/v1/system/formatStorage")
    suspend fun formatStorage(): TS004Response<Boolean>

    /**
     * [Chinese text]Settings
     */
    @POST("/api/v1/system/resetAll")
    suspend fun resetAll(): TS004Response<Boolean>

    /**
     * Settings[Chinese text]
     */
    @POST("/api/v1/system/setTISR")
    suspend fun setTISR(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * [Chinese text]
     */
    @POST("/api/v1/system/getTISR")
    suspend fun getTISR(): TS004Response<TISRBean>
}
