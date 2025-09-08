package com.topdon.house.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.db.entity.DirDetect
import com.topdon.lib.core.db.entity.HouseDetect
import com.topdon.lib.core.db.entity.ItemDetect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * [Chinese text] ViewModel.
 *
 * Created by LCG on 2024/8/22.
 */
class DetectViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * [Chinese text], [Chinese text] [queryAll] [Chinese text].
     */
    val detectListLD =  MutableLiveData<List<HouseDetect>>()
    /**
     * [Chinese text], [Chinese text] [detectListLD] [Chinese text].
     */
    fun queryAll() {
        viewModelScope.launch(Dispatchers.IO) {
            detectListLD.postValue(AppDatabase.getInstance().houseDetectDao().queryAll())
        }
    }

    /**
     * [Chinese text], [Chinese text] [queryById], [insertDefaultDirs] [Chinese text].
     */
    val detectLD = MutableLiveData<HouseDetect?>()
    /**
     * [Chinese text] id [Chinese text], [Chinese text] [detectLD] [Chinese text].
     */
    fun queryById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            detectLD.postValue(AppDatabase.getInstance().houseDetectDao().queryById(id))
        }
    }
    /**
     * [Chinese text], [Chinese text] [detectLD] [Chinese text].
     */
    fun insertDefaultDirs(houseDetect: HouseDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().houseDetectDao().insertDefaultDirs(houseDetect)
            detectLD.postValue(houseDetect)
        }
    }

    /**
     * [Chinese text]message, [Chinese text] [queryDirById] [Chinese text], [Chinese text]message[Chinese text].
     */
    val dirLD = MutableLiveData<DirDetect>()
    /**
     * [Chinese text] id [Chinese text]message, [Chinese text] [dirLD] [Chinese text].
     */
    fun queryDirById(dirId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dirLD.postValue(AppDatabase.getInstance().houseDetectDao().queryDir(dirId))
        }
    }

    /**
     * [Chinese text], [Chinese text] [copyDetect] [Chinese text].
     */
    val copyDetectLD = MutableLiveData<Pair<Int, HouseDetect>>()
    /**
     * [Chinese text], [Chinese text] [copyDetectLD] [Chinese text].
     * @param position [Chinese text], [Chinese text]
     * @param houseDetect [Chinese text]
     */
    fun copyDetect(position: Int, houseDetect: HouseDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            copyDetectLD.postValue(Pair(position, AppDatabase.getInstance().houseDetectDao().copyDetect(houseDetect)))
        }
    }

    /**
     * [Chinese text], [Chinese text] [copyDir] [Chinese text].
     */
    val copyDirLD = MutableLiveData<Pair<Int, DirDetect>>()
    /**
     * [Chinese text]in progress, [Chinese text], [Chinese text] [copyDirLD] [Chinese text].
     * @param layoutIndex [Chinese text], [Chinese text]
     * @param dirDetect [Chinese text]
     */
    fun copyDir(layoutIndex: Int, dirDetect: DirDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            val dirList: ArrayList<DirDetect> = dirDetect.houseDetect.dirList
            val position = dirList.indexOf(dirDetect)
            if (position >= 0) {
                val newDir: DirDetect = AppDatabase.getInstance().houseDetectDao().copyDir(dirList, position)
                dirList.add(position + 1, newDir)
                copyDirLD.postValue(Pair(layoutIndex, newDir))
            }
        }
    }

    /**
     * [Chinese text], [Chinese text] [copyItem] [Chinese text].
     */
    val copyItemLD = MutableLiveData<Pair<Int, ItemDetect>>()
    /**
     * [Chinese text]in progress, [Chinese text], [Chinese text] [copyItemLD] [Chinese text].
     * @param layoutIndex [Chinese text], [Chinese text]
     * @param itemDetect [Chinese text]
     */
    fun copyItem(layoutIndex: Int, itemDetect: ItemDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            val itemList: ArrayList<ItemDetect> = itemDetect.dirDetect.itemList
            val position = itemList.indexOf(itemDetect)
            if (position >= 0) {
                val newItem = AppDatabase.getInstance().houseDetectDao().copyItem(itemList, position)
                itemList.add(position + 1, newItem)
                copyItemLD.postValue(Pair(layoutIndex, newItem))
            }
        }
    }

    /**
     * [Chinese text], [Chinese text] [delItem] [Chinese text].
     */
    val delItemLD = MutableLiveData<Pair<Int, ItemDetect>>()
    /**
     * [Chinese text]in progress, [Chinese text], [Chinese text] [delItemLD] [Chinese text].
     * @param layoutIndex [Chinese text], [Chinese text]
     * @param itemDetect [Chinese text]
     */
    fun delItem(layoutIndex: Int, itemDetect: ItemDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            val dirDetect: DirDetect = itemDetect.dirDetect
            val itemList: ArrayList<ItemDetect> = dirDetect.itemList
            val position = itemList.indexOf(itemDetect)
            if (position >= 0) {
                AppDatabase.getInstance().houseDetectDao().deleteItem(itemDetect)
                itemList.removeAt(position)

                if (itemList.isEmpty()) {// [Chinese text], [Chinese text]
                    val dirList: ArrayList<DirDetect> = dirDetect.houseDetect.dirList
                    val dirPosition = dirList.indexOf(dirDetect)
                    if (dirPosition >= 0) {
                        AppDatabase.getInstance().houseDetectDao().deleteDir(dirDetect)
                        dirList.removeAt(dirPosition)
                    }
                    if (dirList.isEmpty()) {// [Chinese text]
                        detectLD.postValue(dirDetect.houseDetect)
                    } else {
                        delItemLD.postValue(Pair(layoutIndex, itemDetect))
                    }
                } else {
                    // [Chinese text]3[Chinese text]
                    if (itemDetect.state > 0) {
                        val dir = itemDetect.dirDetect
                        when (itemDetect.state) {
                            1 -> dir.goodCount--
                            2 -> dir.warnCount--
                            3 -> dir.dangerCount--
                        }
                        updateDir(dir)
                    }
                    delItemLD.postValue(Pair(layoutIndex, itemDetect))
                }
            }
        }
    }

    /**
     * [Chinese text]message.
     */
    fun updateDir(vararg dirDetect: DirDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().houseDetectDao().updateDir(*dirDetect)
        }
    }
    /**
     * [Chinese text]message.
     */
    fun updateItem(vararg itemDetect: ItemDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().houseDetectDao().updateItem(*itemDetect)
        }
    }

    /**
     * [Chinese text].
     */
    fun deleteMore(vararg houseDetect: HouseDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().houseDetectDao().deleteDetect(*houseDetect)
            queryAll()
        }
    }
}