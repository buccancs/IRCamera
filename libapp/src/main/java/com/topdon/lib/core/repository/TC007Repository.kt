package com.topdon.lib.core.repository

import android.graphics.Point
import android.graphics.Rect
import android.net.Network
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.topdon.lib.core.http.converter.StringConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object TC007Repository {
    private fun Any.toBody(): RequestBody = Gson().toJson(this).toRequestBody()

    var netWork: Network? = null
    var tempFrameParam: TempFrameParam? = null

    private fun getOKHttpClient(timeout: Long): OkHttpClient {
        val builder =
            OkHttpClient.Builder()
                .retryOnConnectionFailure(false) // repository
                .connectTimeout(timeout, TimeUnit.SECONDS) // 2024-5-29 TS004 repository15repository
                .readTimeout(timeout, TimeUnit.SECONDS) // 2024-5-29 TS004 repository15repository
                .writeTimeout(timeout, TimeUnit.SECONDS) // 2024-5-29 TS004 repository15repository
                .addInterceptor(OKLogInterceptor(true))
        netWork?.socketFactory?.let {
            builder.socketFactory(it)
        }
        return builder.build()
    }

    private fun getTC007Service(timeout: Long = 15): TC007Service =
        Retrofit.Builder()
            .baseUrl("http://192.168.40.1:63206")
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(StringConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getOKHttpClient(timeout))
            .build()
            .create(TC007Service::class.java)

    suspend fun setEnvAttr(
        isCelsius: Boolean,
        Level: Int,
    ): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["TempUnit"] = if (isCelsius) 0 else 2 // 0-Celsius 1-repository 2-Fahrenheit
                paramMap["Level"] = Level // 0:High gain 1:Low gain 3:repositorySwitch
                paramMap["Fps"] = 12 // Temperature measurementrepository,repository[0,repository],repository12,repository12repository
                paramMap["OsdMode"] = 1 // Temperature measurementrepository，0:repository 1:repository(repository) 2:repository
                paramMap["DistanceUnit"] = 0 // repository，0:repository 1:repository
                getTC007Service().setEnvAttr(paramMap.toBody()).isSuccess()
            } catch (_: Exception) {
                false
            }
        }

    suspend fun setIRConfig(
        environment: Float,
        distance: Float,
        radiation: Float,
    ): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["AtmosphereTemp"] = ((environment + 273.15f) * 10).toInt()
                paramMap["Distance"] = (distance * 100).toInt()
                paramMap["Emissivity"] = (radiation * 10000).toInt()
//            paramMap["ReflectedTemp"] = 2982
//            paramMap["Transmittance"] = 10000
                getTC007Service().setIRConfig(paramMap.toBody()).isSuccess()
            } catch (_: Exception) {
                false
            }
        }

    /**
     * ClearAllrepository、repository、repository.
     */
    suspend fun clearAllTemp(): Boolean =
        withContext(Dispatchers.IO) {
            try {
                var result = true
                result = result && setTempPointList(ArrayList())
                result = result && setTempLineList(ArrayList())
                result = result && setTempRectList(ArrayList())
                result
            } catch (_: Exception) {
                false
            }
        }

    /**
     * SwitchrepositoryTemperature measurementrepositoryState
     */
    suspend fun getTempFrame(): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val data = getTC007Service().getTempFrame()
                if (data.isSuccess()) {
                    tempFrameParam = data.Data
                }
                data.isSuccess()
            } catch (_: Exception) {
                false
            }
        }

    /**
     * SettingsrepositoryTemperature measurementrepository.
     */
    suspend fun setTempFrame(boolean: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (tempFrameParam != null) {
                    tempFrameParam!!.FrameLow.Enable = boolean
                    tempFrameParam!!.FrameCenter.Enable = boolean
                    tempFrameParam!!.FrameHigh.Enable = boolean
                    getTC007Service().setTempFrame(tempFrameParam!!.toBody()).isSuccess()
                } else {
                    false
                }
            } catch (_: Exception) {
                false
            }
        }

    /**
     * SettingsTemperature measurementrepository.
     */
    suspend fun setTempPointList(pointList: List<Point>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramList = ArrayList<TempPointParam>(3)
                for (i in 0 until 3) {
                    paramList.add(TempPointParam(i + 1, if (i < pointList.size) pointList[i] else null))
                }
                getTC007Service().setTempPoint(paramList.toBody()).isSuccess()
            } catch (_: Exception) {
                false
            }
        }

    /**
     * SettingsTemperature measurementrepository.
     */
    suspend fun setTempLineList(lineList: List<Point>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramList = ArrayList<TempLineParam>(3)
                for (i in 0 until 3) {
                    val startPoint = if (i * 2 < lineList.size) lineList[i * 2] else null
                    val endPoint = if (i * 2 + 1 < lineList.size) lineList[i * 2 + 1] else null
                    paramList.add(TempLineParam(i + 1, startPoint, endPoint))
                }
                getTC007Service().setTempLine(paramList.toBody()).isSuccess()
            } catch (_: Exception) {
                false
            }
        }

    /**
     * SettingsTemperature measurementrepository.
     */
    suspend fun setTempRectList(rectList: List<Rect>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramList = ArrayList<TempRectParam>(3)
                for (i in 0 until 3) {
                    paramList.add(TempRectParam(i + 1, if (i < rectList.size) rectList[i] else null))
                }
                getTC007Service().setTempRect(paramList.toBody()).isSuccess()
            } catch (_: Exception) {
                false
            }
        }

    /**
     * Photo
     */
    suspend fun getPhoto(): TC007Response<PhotoBean>? =
        withContext(Dispatchers.IO) {
            try {
                getTC007Service().getPhoto()
            } catch (e: Exception) {
                XLog.e("repository：${e?.message}")
                null
            }
        }

    /**
     * SettingsrepositoryMode
     */
    suspend fun setMode(mode: Int): TC007Response<Any?>? =
        withContext(Dispatchers.IO) {
            try {
                getTC007Service().setMode(mode)
            } catch (e: Exception) {
                XLog.e("repository：${e?.message}")
                null
            }
        }

    suspend fun getAttribute(default: Boolean): TC007Response<AttributeBean?>? =
        withContext(Dispatchers.IO) {
            try {
                getTC007Service().getAttribute(1, default.toString())
            } catch (e: Exception) {
                null
            }
        }

    suspend fun setRatio(data: Any?): TC007Response<Any?>? =
        withContext(Dispatchers.IO) {
            if (data == null) {
                return@withContext null
            }
            try {
                getTC007Service().setRatio(data.toBody())
            } catch (e: Exception) {
                XLog.e("repository：${e?.message}")
                null
            }
        }

    suspend fun getRegistration(default: Boolean): TC007Response<WifiAttributeBean?>? =
        withContext(Dispatchers.IO) {
            try {
                getTC007Service().getRegistration(1, default.toString())
            } catch (e: Exception) {
                null
            }
        }

    suspend fun setRegistration(data: Any?): TC007Response<Any?>? =
        withContext(Dispatchers.IO) {
            if (data == null) {
                return@withContext null
            }
            try {
                getTC007Service().setRegistration(data.toBody())
            } catch (e: Exception) {
                XLog.e("repository：${e?.message}")
                null
            }
        }

    suspend fun setPallete(data: Any?): TC007Response<Any?>? =
        withContext(Dispatchers.IO) {
            if (data == null) {
                return@withContext null
            }
            try {
                getTC007Service().setPallete(data.toBody())
            } catch (e: Exception) {
                XLog.e("repository：${e?.message}")
                null
            }
        }

    suspend fun setParam(data: Any?): TC007Response<Any?>? =
        withContext(Dispatchers.IO) {
            if (data == null) {
                return@withContext null
            }
            try {
                getTC007Service().setParam(data.toBody())
            } catch (e: Exception) {
                XLog.e("repository：${e?.message}")
                null
            }
        }

    suspend fun setFont(data: Any?): TC007Response<Any?>? =
        withContext(Dispatchers.IO) {
            if (data == null) {
                return@withContext null
            }
            try {
                getTC007Service().setFont(data.toBody())
            } catch (e: Exception) {
                XLog.e("repository：${e?.message}")
                null
            }
        }

    suspend fun setCorrection(): TC007Response<Any?>? =
        withContext(Dispatchers.IO) {
            try {
                getTC007Service().setCorrection()
            } catch (e: Exception) {
                XLog.e("repository：${e?.message}")
                null
            }
        }

    suspend fun setIsotherm(data: Any?): TC007Response<Any?>? =
        withContext(Dispatchers.IO) {
            if (data == null) {
                return@withContext null
            }
            try {
                getTC007Service().setIsotherm(data.toBody())
            } catch (e: Exception) {
                XLog.e("repository：${e?.message}")
                null
            }
        }
}
