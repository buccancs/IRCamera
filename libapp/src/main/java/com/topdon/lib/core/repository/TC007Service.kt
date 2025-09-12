package com.topdon.lib.core.repository

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 * Retrofit service interface for TC007 device API
 */
interface TC007Service {

    @GET("/v1/system/upgrade/status")
    suspend fun getUpgradeStatus(): TC007Response<TC07UpgradeStatus>

    /**
     * dataSettings
     */
    @PUT("/v1/system/magic/factory")
    suspend fun resetToFactory(): TC007Response<Boolean>

    /**
     * data
     */
    @PUT("/v1/camera/videoin/thermal/lid")
    suspend fun correction(): TC007Response<Any?>

    /**
     * dataTemperature measurementdata
     */
    @GET("/v1/thermal/env/attribute?default=false")
    suspend fun getEnvAttr(): TC007Response<EnvAttr>

    /**
     * SettingsTemperature measurementdata
     */
    @PUT("/v1/thermal/env/attribute?default=false")
    suspend fun setEnvAttr(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    /**
     * Settingsdata
     */
    @PUT("/v1/thermal/env/target")
    suspend fun setIRConfig(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    @GET("/v1/thermal/temp/frame")
    suspend fun getTempFrame(): TC007Response<TempFrameParam>

    /**
     * SettingsdataTemperature measurement（data、dataHigh temperature、dataLow temperature）
     */
    @POST("/v1/thermal/temp/frame")
    suspend fun setTempFrame(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    /**
     * SettingsTemperature measurementdata
     */
    @POST("/v1/thermal/temp/point")
    suspend fun setTempPoint(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    /**
     * SettingsTemperature measurementdata
     */
    @POST("/v1/thermal/temp/line")
    suspend fun setTempLine(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    /**
     * SettingsTemperature measurementdata
     */
    @POST("/v1/thermal/temp/rectangle")
    suspend fun setTempRect(
        @Body requestBody: RequestBody,
    ): TC007Response<Any?>

    /**
     * Photo
     */
    @PUT("/v1/storage/picture/snap/manual")
    suspend fun getPhoto(): TC007Response<PhotoBean>

    /**
     * SettingsdataMode
     * 0：Infrared；1：Visible light；2：Picture in picture；3：Dual lightdata；4：data
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

    // Dual lightRegistration
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
     * dataTemperature measurementdata
     * chn
     * integer
     * data
     * data
     * data:
     * 1
     * default
     * string
     * data
     * true：data；false：Currentdata
     * data:
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
