package com.topdon.lib.core.repository

import android.net.Network
import com.blankj.utilcode.util.EncryptUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object TS004Repository {
    private fun Any.toBody(): RequestBody = Gson().toJson(this).toRequestBody()

    var netWork: Network? = null

    private fun getOKHttpClient(): OkHttpClient {
        val build =
            OkHttpClient.Builder()
                .retryOnConnectionFailure(false) // [CN_TEXT]
                .connectTimeout(15, TimeUnit.SECONDS) // 2024-5-29 TS004 [CN_TEXT]15[CN_TEXT]
                .readTimeout(15, TimeUnit.SECONDS) // 2024-5-29 TS004 [CN_TEXT]15[CN_TEXT]
                .writeTimeout(15, TimeUnit.SECONDS) // 2024-5-29 TS004 [CN_TEXT]15[CN_TEXT]
                .addInterceptor(OKLogInterceptor(false))
        netWork?.socketFactory?.let {
            build.socketFactory(it)
        }

        return build.build()
    }

    private fun getTS004Service(): TS004Service =
        Retrofit.Builder()
            .baseUrl("http://192.168.40.1:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getOKHttpClient())
            .build()
            .create(TS004Service::class.java)

    /**
     * [CN_TEXT]
     * @param dataMap key-URL，value-[CN_TEXT]
     * @param listener [CN_TEXT]，[CN_TEXT]
     */
    suspend fun downloadList(
        dataMap: Map<String, File>,
        listener: ((path: String, isSuccess: Boolean) -> Unit),
    ): Int {
        return withContext(Dispatchers.IO) {
            var successCount = 0
            dataMap.forEach {
                val isSuccess = download(it.key, it.value)
                launch(Dispatchers.Main) {
                    listener.invoke(it.key, isSuccess)
                }
                if (isSuccess) {
                    successCount++
                }
            }
            return@withContext successCount
        }
    }

    suspend fun download(
        url: String,
        file: File,
    ): Boolean =
        withContext(Dispatchers.IO) {
            val responseBody =
                try {
                    getTS004Service().download(url)
                } catch (_: Exception) {
                    return@withContext false
                }
            var inputStream: InputStream? = null
            var fileOutputString: FileOutputStream? = null
            try {
                inputStream = responseBody.byteStream()
                fileOutputString = FileOutputStream(file)

                val buffer = ByteArray(4096)
                var readLength = inputStream.read(buffer)
                while (readLength != -1) {
                    fileOutputString.write(buffer, 0, readLength)
                    readLength = inputStream.read(buffer)
                }
                fileOutputString.flush()

                return@withContext true
            } catch (_: Exception) {
                return@withContext false
            } finally {
                inputStream?.close()
                fileOutputString?.close()
            }
        }

    /**
     * [CN_TEXT].
     */
    suspend fun syncTime(): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val calendar = Calendar.getInstance()
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["year"] = calendar.get(Calendar.YEAR)
                paramMap["month"] = calendar.get(Calendar.MONTH) + 1
                paramMap["day"] = calendar.get(Calendar.DAY_OF_MONTH)
                paramMap["hour"] = calendar.get(Calendar.HOUR_OF_DAY)
                paramMap["min"] = calendar.get(Calendar.MINUTE)
                paramMap["sec"] = calendar.get(Calendar.SECOND)
                paramMap["usec"] = calendar.get(Calendar.MILLISECOND)
                getTS004Service().syncTime(paramMap.toBody()).isSuccess()
            } catch (_: Exception) {
                false
            }
        }

    /**
     * [CN_TEXT].
     */
    suspend fun syncTimeZone(): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["timezone"] = TimeZone.getDefault().rawOffset / 1000 / 60 / 60
                getTS004Service().syncTimeZone(paramMap.toBody()).isSuccess()
            } catch (_: Exception) {
                false
            }
        }

    /**
     * [CN_TEXT]
     */
    suspend fun getVersion(): TS004Response<VersionBean>? =
        withContext(Dispatchers.IO) {
            try {
                getTS004Service().getVersion()
            } catch (_: Exception) {
                null
            }
        }

    /**
     * [CN_TEXT]
     */
    suspend fun getDeviceInfo(): TS004Response<DeviceInfo>? =
        withContext(Dispatchers.IO) {
            try {
                getTS004Service().getDeviceInfo()
            } catch (_: Exception) {
                null
            }
        }

    /**
     * [CN_TEXT].
     * @param fileType 0-[CN_TEXT] 1-Video 2-All
     */
    suspend fun getFileCount(fileType: Int): Int? =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["fileType"] = fileType
                getTS004Service().getFileCount(paramMap.toBody()).data?.fileCount ?: 0
            } catch (e: Exception) {
                null
            }
        }

    /**
     * [CN_TEXT]SpecifiedType[CN_TEXT].
     * @param fileType 0-[CN_TEXT] 1-Video 2-All
     */
    suspend fun getNewestFile(fileType: Int): List<FileBean>? =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["pageNum"] = 1
                paramMap["pageCount"] = 1
                paramMap["fileType"] = fileType
                getTS004Service().getFileList(paramMap.toBody()).data?.filelist ?: return@withContext ArrayList()
            } catch (_: Exception) {
                null
            }
        }

    /**
     * [CN_TEXT]SpecifiedType[CN_TEXT]All[CN_TEXT].
     * @param fileType 0-[CN_TEXT] 1-Video 2-All
     */
    suspend fun getAllFileList(fileType: Int): List<FileBean> =
        withContext(Dispatchers.IO) {
            try {
                val fileCount = getFileCount(fileType) ?: return@withContext ArrayList()
                if (fileCount < 1) {
                    return@withContext ArrayList()
                }

                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["pageNum"] = 1
                paramMap["pageCount"] = fileCount
                paramMap["fileType"] = fileType
                getTS004Service().getFileList(paramMap.toBody()).data?.filelist ?: ArrayList()
            } catch (_: Exception) {
                ArrayList()
            }
        }

    /**
     * [CN_TEXT]SpecifiedType[CN_TEXT].
     * @param fileType 0-[CN_TEXT] 1-Video 2-All
     * @return null-[CN_TEXT]
     */
    suspend fun getFileByPage(
        fileType: Int,
        pageNum: Int,
        pageCount: Int,
    ): List<FileBean>? =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["pageNum"] = pageNum
                paramMap["pageCount"] = pageCount
                paramMap["fileType"] = fileType
                getTS004Service().getFileList(paramMap.toBody()).data?.filelist ?: ArrayList()
            } catch (_: Exception) {
                null
            }
        }

    data class IdData(val id: Int)

    /**
     * DeleteSpecified id [CN_TEXT]
     */
    suspend fun deleteFiles(ids: Array<Int>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val idArray: Array<IdData> =
                    Array(ids.size) {
                        IdData(ids[it])
                    }

                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["filelist"] = idArray
                getTS004Service().deleteFile(paramMap.toBody()).isSuccess()
            } catch (_: Exception) {
                false
            }
        }

    /**
     * [CN_TEXT].
     */
    suspend fun updateFirmware(file: File): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val isStartSuccess = getTS004Service().firmwareUpdateStart().isSuccess()
                if (!isStartSuccess) {
                    return@withContext false
                }

                val isSendStartSuccess = sendUpgradeFileStart(file)
                if (!isSendStartSuccess) {
                    return@withContext false
                }

                val isSendFileSuccess = sendUpgradeFile(file)
                if (!isSendFileSuccess) {
                    return@withContext false
                }

                val isEndSuccess = sendUpgradeFileEnd(file)
                if (!isEndSuccess) {
                    return@withContext false
                }

                var status = getTS004Service().getUpgradeStatus().data?.status
                while (status == 0 || status == 1 || status == 2) { // [CN_TEXT]
                    delay(1000)
                    status = getTS004Service().getUpgradeStatus().data?.status
                }

                status == 4
            } catch (_: Exception) {
                false
            }
        }

    private suspend fun sendUpgradeFileStart(file: File): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["saveAsFile"] = true
                paramMap["MD5"] = EncryptUtils.encryptMD5File2String(file).lowercase(Locale.ROOT)
                paramMap["length"] = file.length()
                getTS004Service().sendUpgradeFileStart(paramMap.toBody()).isSuccess()
            } catch (_: Exception) {
                false
            }
        }

    private suspend fun sendUpgradeFile(file: File): Boolean =
        withContext(Dispatchers.IO) {
            var fileInputStream: FileInputStream? = null
            try {
                fileInputStream = FileInputStream(file)

                var hasReadCount = 0
                var byteArray = ByteArray(1024 * 1024 * 5) // 5M[CN_TEXT]

                var readCount = fileInputStream.read(byteArray)
                while (readCount != -1) {
                    hasReadCount += readCount
                    if (hasReadCount == 1024 * 1024 * 5) {
                        getTS004Service().sendUpgradeFile(byteArray.toRequestBody())
                        hasReadCount = 0
                        byteArray = ByteArray(1024 * 1024 * 5) // 5M[CN_TEXT]
                    }
                    readCount = fileInputStream.read(byteArray, hasReadCount, byteArray.size - hasReadCount)
                }

                if (hasReadCount > 0) {
                    val lastArray = ByteArray(hasReadCount)
                    System.arraycopy(byteArray, 0, lastArray, 0, hasReadCount)
                    getTS004Service().sendUpgradeFile(lastArray.toRequestBody())
                }

                true
            } catch (_: Exception) {
                false
            } finally {
                fileInputStream?.close()
            }
        }

    private suspend fun sendUpgradeFileEnd(file: File): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["MD5"] = EncryptUtils.encryptMD5File2String(file).lowercase(Locale.ROOT)
                getTS004Service().sendUpgradeFileEnd(paramMap.toBody()).isSuccess()
            } catch (_: Exception) {
                false
            }
        }

    /**
     * SettingsPseudo-color[CN_TEXT]
     * @param mode Pseudo-color[CN_TEXT] White hot-1，Black hot-2，Red hot-9, Iron red-5
     */
    suspend fun setPseudoColor(mode: Int): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["enable"] = false
                paramMap["mode"] = mode
                getTS004Service().setPseudoColor(paramMap.toBody()).isSuccess()
            } catch (e: Exception) {
                false
            }
        }

    /**
     * [CN_TEXT]Pseudo-color[CN_TEXT]
     */
    suspend fun getPseudoColor(): TS004Response<PseudoColorBean>? =
        withContext(Dispatchers.IO) {
            try {
                getTS004Service().getPseudoColor()
            } catch (_: Exception) {
                null
            }
        }

    /**
     * Settings[CN_TEXT]
     * @param state 0-[CN_TEXT]，1-[CN_TEXT]
     */
    suspend fun setRangeFind(state: Int): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["state"] = state
                getTS004Service().setRangeFind(paramMap.toBody()).isSuccess()
            } catch (e: Exception) {
                false
            }
        }

    /**
     * [CN_TEXT]
     */
    suspend fun getRangeFind(): TS004Response<RangeBean>? =
        withContext(Dispatchers.IO) {
            try {
                getTS004Service().getRangeFind()
            } catch (_: Exception) {
                null
            }
        }

    /**
     * Settings[CN_TEXT]
     * @param brightness  [CN_TEXT]:[CN_TEXT]0-100
     */
    suspend fun setPanelParam(brightness: Int): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["brightness"] = brightness
                getTS004Service().setPanelParam(paramMap.toBody()).isSuccess()
            } catch (e: Exception) {
                false
            }
        }

    /**
     * [CN_TEXT]
     */
    suspend fun getPanelParam(): TS004Response<BrightnessBean>? =
        withContext(Dispatchers.IO) {
            try {
                getTS004Service().getPanelParam()
            } catch (_: Exception) {
                null
            }
        }

    /**
     * SettingsPicture in picture
     * @param enable  true [CN_TEXT]，false [CN_TEXT]
     */
    suspend fun setPip(enable: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["enable"] = enable
                getTS004Service().setPip(paramMap.toBody()).isSuccess()
            } catch (e: Exception) {
                false
            }
        }

    /**
     * [CN_TEXT]Picture in picture
     */
    suspend fun getPip(): TS004Response<PipBean>? =
        withContext(Dispatchers.IO) {
            try {
                getTS004Service().getPip()
            } catch (_: Exception) {
                null
            }
        }

    /**
     * Settings[CN_TEXT]
     * @param factor [CN_TEXT]:1,2,4,8
     */
    suspend fun setZoom(factor: Int): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["enable"] = true
                paramMap["factor"] = factor
                getTS004Service().setZoom(paramMap.toBody()).isSuccess()
            } catch (e: Exception) {
                false
            }
        }

    /**
     * [CN_TEXT]
     */
    suspend fun getZoom(): TS004Response<ZoomBean>? =
        withContext(Dispatchers.IO) {
            try {
                getTS004Service().getZoom()
            } catch (_: Exception) {
                null
            }
        }

    /**
     * SettingsPhoto
     * @param factor [CN_TEXT]:1,2,4,8
     */
    suspend fun setSnapshot(): Boolean =
        withContext(Dispatchers.IO) {
            try {
                getTS004Service().setSnapshot().isSuccess()
            } catch (e: Exception) {
                false
            }
        }

    /**
     * SettingsVideo
     * @param enable [CN_TEXT]
     */
    suspend fun setVideo(enable: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["enable"] = enable
                getTS004Service().setVRecord(paramMap.toBody()).isSuccess()
            } catch (e: Exception) {
                false
            }
        }

    /**
     * [CN_TEXT]State
     */
    suspend fun getRecordStatus(): TS004Response<RecordStatusBean>? =
        withContext(Dispatchers.IO) {
            try {
                getTS004Service().getVRecord()
            } catch (_: Exception) {
                null
            }
        }

    /**
     * [CN_TEXT]
     */
    suspend fun getFreeSpace(): FreeSpaceBean? =
        withContext(Dispatchers.IO) {
            try {
                getTS004Service().freeSpace().data
            } catch (_: Exception) {
                null
            }
        }

    /**
     * [CN_TEXT]
     */
    suspend fun getFormatStorage(): Boolean =
        withContext(Dispatchers.IO) {
            try {
                getTS004Service().formatStorage().isSuccess()
            } catch (_: Exception) {
                false
            }
        }

    /**
     * [CN_TEXT]Settings
     */
    suspend fun getResetAll(): Boolean =
        withContext(Dispatchers.IO) {
            try {
                // [CN_TEXT]Historical legacy[CN_TEXT]，[CN_TEXT] status 0 [CN_TEXT]，[CN_TEXT]，100 [CN_TEXT]
                getTS004Service().resetAll().status == 100
            } catch (_: Exception) {
                false
            }
        }

    /**
     * Settings[CN_TEXT]
     * @param state 0-[CN_TEXT] 1-[CN_TEXT]
     */
    suspend fun setTISR(state: Int): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val paramMap: HashMap<String, Any> = HashMap()
                paramMap["state"] = state
                getTS004Service().setTISR(paramMap.toBody()).isSuccess()
            } catch (e: Exception) {
                false
            }
        }

    /**
     * [CN_TEXT]State
     */
    suspend fun getTISR(): TS004Response<TISRBean>? =
        withContext(Dispatchers.IO) {
            try {
                getTS004Service().getTISR()
            } catch (_: Exception) {
                null
            }
        }
}
