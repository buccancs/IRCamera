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
 * [CN_TEXT] ViewModel.
 *
 * Created by LCG on 2024/8/22.
 */
class DetectViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * All[CN_TEXT]，[CN_TEXT] [queryAll] [CN_TEXT].
     */
    val detectListLD =  MutableLiveData<List<HouseDetect>>()
    /**
     * [CN_TEXT]All[CN_TEXT]，[CN_TEXT] [detectListLD] [CN_TEXT].
     */
    fun queryAll() {
        viewModelScope.launch(Dispatchers.IO) {
            detectListLD.postValue(AppDatabase.getInstance().houseDetectDao().queryAll())
        }
    }


    /**
     * [CN_TEXT]，[CN_TEXT] [queryById]、[insertDefaultDirs] [CN_TEXT].
     */
    val detectLD = MutableLiveData<HouseDetect?>()
    /**
     * [CN_TEXT]Specified id [CN_TEXT]，[CN_TEXT] [detectLD] [CN_TEXT].
     */
    fun queryById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            detectLD.postValue(AppDatabase.getInstance().houseDetectDao().queryById(id))
        }
    }
    /**
     * [CN_TEXT]Specified[CN_TEXT]，[CN_TEXT] [detectLD] [CN_TEXT].
     */
    fun insertDefaultDirs(houseDetect: HouseDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().houseDetectDao().insertDefaultDirs(houseDetect)
            detectLD.postValue(houseDetect)
        }
    }


    /**
     * [CN_TEXT]，[CN_TEXT] [queryDirById] [CN_TEXT]，Note[CN_TEXT].
     */
    val dirLD = MutableLiveData<DirDetect>()
    /**
     * [CN_TEXT]Specified id [CN_TEXT]，[CN_TEXT] [dirLD] [CN_TEXT].
     */
    fun queryDirById(dirId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dirLD.postValue(AppDatabase.getInstance().houseDetectDao().queryDir(dirId))
        }
    }


    /**
     * [CN_TEXT]，[CN_TEXT] [copyDetect] [CN_TEXT].
     */
    val copyDetectLD = MutableLiveData<Pair<Int, HouseDetect>>()
    /**
     * [CN_TEXT]Specified[CN_TEXT]，[CN_TEXT] [copyDetectLD] [CN_TEXT].
     * @param position [CN_TEXT]，[CN_TEXT]
     * @param houseDetect [CN_TEXT]
     */
    fun copyDetect(position: Int, houseDetect: HouseDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            copyDetectLD.postValue(Pair(position, AppDatabase.getInstance().houseDetectDao().copyDetect(houseDetect)))
        }
    }

    /**
     * [CN_TEXT]，[CN_TEXT] [copyDir] [CN_TEXT].
     */
    val copyDirLD = MutableLiveData<Pair<Int, DirDetect>>()
    /**
     * [CN_TEXT]Specified[CN_TEXT]，[CN_TEXT]Specified[CN_TEXT]，[CN_TEXT] [copyDirLD] [CN_TEXT].
     * @param layoutIndex [CN_TEXT]，[CN_TEXT]
     * @param dirDetect [CN_TEXT]
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
     * [CN_TEXT]，[CN_TEXT] [copyItem] [CN_TEXT].
     */
    val copyItemLD = MutableLiveData<Pair<Int, ItemDetect>>()
    /**
     * [CN_TEXT]Specified[CN_TEXT]，[CN_TEXT]Specified[CN_TEXT]，[CN_TEXT] [copyItemLD] [CN_TEXT].
     * @param layoutIndex [CN_TEXT]，[CN_TEXT]
     * @param itemDetect [CN_TEXT]
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
     * Delete[CN_TEXT]，[CN_TEXT] [delItem] [CN_TEXT].
     */
    val delItemLD = MutableLiveData<Pair<Int, ItemDetect>>()
    /**
     * [CN_TEXT]Specified[CN_TEXT]，DeleteSpecified[CN_TEXT]，[CN_TEXT] [delItemLD] [CN_TEXT].
     * @param layoutIndex [CN_TEXT]，[CN_TEXT]
     * @param itemDetect [CN_TEXT]Delete[CN_TEXT]
     */
    fun delItem(layoutIndex: Int, itemDetect: ItemDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            val dirDetect: DirDetect = itemDetect.dirDetect
            val itemList: ArrayList<ItemDetect> = dirDetect.itemList
            val position = itemList.indexOf(itemDetect)
            if (position >= 0) {
                AppDatabase.getInstance().houseDetectDao().deleteItem(itemDetect)
                itemList.removeAt(position)

                if (itemList.isEmpty()) {//[CN_TEXT]，[CN_TEXT]
                    val dirList: ArrayList<DirDetect> = dirDetect.houseDetect.dirList
                    val dirPosition = dirList.indexOf(dirDetect)
                    if (dirPosition >= 0) {
                        AppDatabase.getInstance().houseDetectDao().deleteDir(dirDetect)
                        dirList.removeAt(dirPosition)
                    }
                    if (dirList.isEmpty()) {//[CN_TEXT]
                        detectLD.postValue(dirDetect.houseDetect)
                    } else {
                        delItemLD.postValue(Pair(layoutIndex, itemDetect))
                    }
                } else {
                    //Delete[CN_TEXT]3[CN_TEXT]
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
     * [CN_TEXT].
     */
    fun updateDir(vararg dirDetect: DirDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().houseDetectDao().updateDir(*dirDetect)
        }
    }
    /**
     * [CN_TEXT].
     */
    fun updateItem(vararg itemDetect: ItemDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().houseDetectDao().updateItem(*itemDetect)
        }
    }


    /**
     * DeleteSpecified[CN_TEXT].
     */
    fun deleteMore(vararg houseDetect: HouseDetect) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().houseDetectDao().deleteDetect(*houseDetect)
            queryAll()
        }
    }
}