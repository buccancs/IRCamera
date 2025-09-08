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
     * SettingsPseudo color样式
     */
    @POST("/api/v1/system/setPseudoColor")
    suspend fun setPseudoColor(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * 获取Pseudo color样式
     */
    @POST("/api/v1/system/getPseudoColor")
    suspend fun getPseudoColor(): TS004Response<PseudoColorBean>

    /**
     * Settings测距开关
     */
    @POST("/api/v1/system/setRangeFind")
    suspend fun setRangeFind(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * 获取测距
     */
    @POST("/api/v1/system/getRangeFind")
    suspend fun getRangeFind(): TS004Response<RangeBean>

    /**
     * Settings屏幕亮度
     */
    @POST("/api/v1/system/setPanelParam")
    suspend fun setPanelParam(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * 获取屏幕亮度
     */
    @POST("/api/v1/system/getPanelParam")
    suspend fun getPanelParam(): TS004Response<BrightnessBean>

    /**
     * Settings画中画
     */
    @POST("/api/v1/system/setPip")
    suspend fun setPip(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * 获取画中画状态
     */
    @POST("/api/v1/system/getPip")
    suspend fun getPip(): TS004Response<PipBean>

    /**
     * Settings放大倍数
     */
    @POST("/api/v1/system/setZoom")
    suspend fun setZoom(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * 获取放大倍数
     */
    @POST("/api/v1/system/getZoom")
    suspend fun getZoom(): TS004Response<ZoomBean>

    /**
     * SettingsPhoto capture
     */
    @POST("/api/v1/system/snapshot")
    suspend fun setSnapshot(): TS004Response<Boolean>

    /**
     * Settings录像
     */
    @POST("/api/v1/system/vrecord")
    suspend fun setVRecord(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * 获取录像状态
     */
    @POST("/api/v1/system/getRecordStatus")
    suspend fun getVRecord(): TS004Response<RecordStatusBean>

    /**
     * 下载文件.
     */
    @GET
    @Streaming
    suspend fun download(
        @Url url: String,
    ): ResponseBody

    /**
     * 同步时间.
     */
    @POST("/api/v1/system/setDateTime")
    suspend fun syncTime(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * 同步时区.
     */
    @POST("/api/v1/system/setTimeZone")
    suspend fun syncTimeZone(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * 获取版本message
     */
    @POST("/api/v1/system/getVersion")
    suspend fun getVersion(): TS004Response<VersionBean>

    /**
     * 获取设备message
     */
    @POST("/api/v1/system/getDeviceInfo")
    suspend fun getDeviceInfo(): TS004Response<DeviceInfo>

    /**
     * 获取文件数量
     */
    @POST("/api/v1/system/getFileCount")
    suspend fun getFileCount(
        @Body requestBody: RequestBody,
    ): TS004Response<FileCountBean>

    /**
     * 获取文件列表
     */
    @POST("/api/v1/system/getFileList")
    suspend fun getFileList(
        @Body requestBody: RequestBody,
    ): TS004Response<FilePageBean>

    /**
     * 删除指定 id 的照片视频文件
     */
    @POST("/api/v1/system/deleteFile")
    suspend fun deleteFile(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * 触发固件升级
     */
    @POST("/api/v1/system/remoteUpgrade")
    suspend fun firmwareUpdateStart(): TS004Response<Boolean>

    /**
     * 固件升级-开始
     */
    @POST("/api/v1/system/sendUpgradeFileStart")
    suspend fun sendUpgradeFileStart(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * 固件升级-上传固件升级包
     */
    @Headers("Content-type: application/octet-stream")
    @POST("/api/v1/system/sendUpgradeFileData")
    suspend fun sendUpgradeFile(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * 固件升级-结束
     */
    @POST("/api/v1/system/sendUpgradeFileEnd")
    suspend fun sendUpgradeFileEnd(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * 查询固件升级状态.
     */
    @POST("/api/v1/system/getUpgradeStatus")
    suspend fun getUpgradeStatus(): TS004Response<UpgradeStatus>

    /**
     * 获取存储分区message
     */
    @POST("/api/v1/system/getFreeSpace")
    suspend fun freeSpace(): TS004Response<FreeSpaceBean>

    /**
     * 格式化存储分区
     */
    @POST("/api/v1/system/formatStorage")
    suspend fun formatStorage(): TS004Response<Boolean>

    /**
     * 恢复出厂Settings
     */
    @POST("/api/v1/system/resetAll")
    suspend fun resetAll(): TS004Response<Boolean>

    /**
     * Settings超分
     */
    @POST("/api/v1/system/setTISR")
    suspend fun setTISR(
        @Body requestBody: RequestBody,
    ): TS004Response<Boolean>

    /**
     * 获取超分
     */
    @POST("/api/v1/system/getTISR")
    suspend fun getTISR(): TS004Response<TISRBean>
}
