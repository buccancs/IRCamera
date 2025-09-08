package com.topdon.lib.core.repository

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

/**
 *
 * Created by LCG on 2024/4/28.
 */
interface TC007Service {
    /**
     * [Chinese text]message
     */
    @GET("/v1/system/product/info/dj")
    suspend fun getProductInfo(): TC007Response<ProductBean>

    /**
     * [Chinese text]message
     */
    @GET("/v1/system/local/battery")
    suspend fun getBatteryInfo(): TC007Response<BatteryInfo>

    /**
     * [Chinese text].
     */
    @PUT("/v1/system/local/time")
    suspend fun syncTime(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    /**
     * [Chinese text]-[Chinese text]
     */
    @Multipart
    @POST("/v1/system/upgrade/package?reset=true")
    suspend fun sendUpgradeFile(
        @Query("filename") filename: String,
        @Query("fileNumber") fileNumber: Int,
        @Query("totalNumber") totalNumber: Int,
        @Query("md5") md5: String,
        @Part part: MultipartBody.Part,
    ): TC007Response<Any?>

    /**
     * [Chinese text].
     */
    @GET("/v1/system/upgrade/status")
    suspend fun getUpgradeStatus(): TC007Response<TC07UpgradeStatus>

    /**
     * [Chinese text]Settings
     */
    @PUT("/v1/system/magic/factory")
    suspend fun resetToFactory(): TC007Response<Boolean>

    /**
     * [Chinese text]
     */
    @PUT("/v1/camera/videoin/thermal/lid")
    suspend fun correction(): TC007Response<Any?>

    /**
     * [Chinese text]
     */
    @GET("/v1/thermal/env/attribute?default=false")
    suspend fun getEnvAttr(): TC007Response<EnvAttr>

    /**
     * Settings[Chinese text]
     */
    @PUT("/v1/thermal/env/attribute?default=false")
    suspend fun setEnvAttr(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    /**
     * Settingstemperature[Chinese text]
     */
    @PUT("/v1/thermal/env/target")
    suspend fun setIRConfig(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    @GET("/v1/thermal/temp/frame")
    suspend fun getTempFrame(): TC007Response<TempFrameParam>

    /**
     * Settings[Chinese text](in progress[Chinese text]point, [Chinese text]high[Chinese text], [Chinese text]low[Chinese text])
     */
    @POST("/v1/thermal/temp/frame")
    suspend fun setTempFrame(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    /**
     * Settings[Chinese text]point
     */
    @POST("/v1/thermal/temp/point")
    suspend fun setTempPoint(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    /**
     * Settings[Chinese text]line
     */
    @POST("/v1/thermal/temp/line")
    suspend fun setTempLine(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    /**
     * Settings[Chinese text]
     */
    @POST("/v1/thermal/temp/rectangle")
    suspend fun setTempRect(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    /**
     * Photo capture
     */
    @PUT("/v1/storage/picture/snap/manual")
    suspend fun getPhoto(): TC007Response<PhotoBean>

    /**
     * Settings[Chinese text]mode
     * 0: [Chinese text]; 1: visible[Chinese text]; 2: [Chinese text]in progress[Chinese text]; 3: Dual light[Chinese text]; 4: [Chinese text]
     */
    @PUT("/v1/camera/videoin/mode")
    suspend fun setMode(
        @Query("mode") mode: Int,
    ): TC007Response<Any?>

    @GET("/v1/camera/videoin/mode")
    suspend fun getMode(
        @Query("mode") mode: Int,
    ): TC007Response<Any?>

    @GET("/v1/camera/videoin/fusion/ratio")
    suspend fun getRatio(
        @Query("default") default: String,
    ): TC007Response<WifiAttributeBean?>

    @PUT("/v1/camera/videoin/fusion/ratio")
    suspend fun setRatio(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    @PUT("/v1/camera/videoin/registration")
    suspend fun setRegistration(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    // Dual light[Chinese text]
    @GET("/v1/camera/videoin/registration")
    suspend fun getRegistration(
        @Query("chn") mode: Int,
        @Query("default") default: String,
    ): TC007Response<WifiAttributeBean?>

    @PUT("/v1/camera/videoin/thermal/pallete/dj")
    suspend fun setPallete(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    /**
     * [Chinese text]
     * chn
     * integer
     * [Chinese text]
     * [Chinese text]
     * [Chinese text]:
     * 1
     * default
     * string
     * [Chinese text]
     * true: [Chinese text]; false: [Chinese text]
     * [Chinese text]:
     * false
     */
    @GET("/v1/thermal/env/attribute")
    suspend fun getAttribute(
        @Query("chn") mode: Int,
        @Query("default") default: String,
    ): TC007Response<AttributeBean?>

    @POST("/v1/camera/videoin/param")
    suspend fun setParam(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    @POST("/v1/system/local/font")
    suspend fun setFont(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    @PUT("/v1/camera/videoin/thermal/correction")
    suspend fun setCorrection(): TC007Response<Any?>

    @POST("/v1/thermal/temp/isotherm")
    suspend fun setIsotherm(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>
}
