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
                .retryOnConnectionFailure(false) // repository
                .connectTimeout(15, TimeUnit.SECONDS) // 2024-5-29 TS004 repository15repository
                .readTimeout(15, TimeUnit.SECONDS) // 2024-5-29 TS004 repository15repository
                .writeTimeout(15, TimeUnit.SECONDS) // 2024-5-29 TS004 repository15repository
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

    suspend fun setSnapshot(): Boolean =
        withContext(Dispatchers.IO) {
            try {
                getTS004Service().setSnapshot().isSuccess()
            } catch (e: Exception) {
                false
            }
        }

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
     * repositoryState
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
