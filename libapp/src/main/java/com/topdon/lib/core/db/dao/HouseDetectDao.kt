package com.topdon.lib.core.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.topdon.lib.core.db.entity.DirDetect
import com.topdon.lib.core.db.entity.HouseDetect
import com.topdon.lib.core.db.entity.ItemDetect

/**
 * Comment removed (contained Chinese characters)
 *
 * Created by LCG on 2024/8/19.
 */
@Dao
abstract class HouseDetectDao {
 /**
 * Comment removed (contained Chinese characters)
 */
 @Transaction
 open fun insert(houseDetect: HouseDetect): Long {
 val id: Long = insertDetect(houseDetect)
 val dirList: ArrayList<DirDetect> = DirDetect.buildDefaultDirList(parentId = id)
 for (i in dirList.indices) {
 val dirId = insertDir(dirList[i])
 val itemList: ArrayList<ItemDetect> = ItemDetect.buildDefaultItemList(dirId, i)
 for (item in itemList) {
 insertItem(item)
 }
 }
 return id
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @Transaction
 open fun insertDefaultDirs(houseDetect: HouseDetect) {
 houseDetect.dirList = DirDetect.buildDefaultDirList(parentId = houseDetect.id)
 for (i in houseDetect.dirList.indices) {
 val dir: DirDetect = houseDetect.dirList[i]
 dir.id = insertDir(dir)
 dir.houseDetect = houseDetect
 dir.itemList = ItemDetect.buildDefaultItemList(dir.id, i)
 for (item in dir.itemList) {
 item.id = insertItem(item)
 item.dirDetect = dir
 }
 }
 }

 @Transaction
 open fun queryById(id: Long): HouseDetect? {
 val houseDetect: HouseDetect = queryDetectById(id) ?: return null
 val dirList: List<DirDetect> = queryDirList(id)
 for (dir in dirList) {
 val itemList: List<ItemDetect> = queryItemList(dir.id)
 for (item in itemList) {
 item.dirDetect = dir
 }
 dir.houseDetect = houseDetect
 dir.itemList = ArrayList(itemList)
 }
 houseDetect.dirList = ArrayList(dirList)
 return houseDetect
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 open fun queryDir(dirId: Long): DirDetect? {
 val dir: DirDetect = queryDirById(dirId) ?: return null
 val itemList: List<ItemDetect> = queryItemList(dirId)
 for (item in itemList) {
 item.dirDetect = dir
 }
 dir.itemList = ArrayList(itemList)
 return dir
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 open fun refreshDetect(houseDetect: HouseDetect) {
 val oldDirList: ArrayList<DirDetect> = ArrayList(queryDirList(houseDetect.id))
 for (i in houseDetect.dirList.indices) {
 val dir = houseDetect.dirList[i]
 dir.position = i
 if (dir.id == 0L) { // 
 dir.id = insertDir(dir)
 for (item in dir.itemList) {
 item.parentId = dir.id
 item.id = insertItem(item)
 item.dirDetect = dir
 }
 } else {
 updateDir(dir)
 oldDirList.remove(dir)
 }
 }
 for (delDir in oldDirList) {
 deleteDir(delDir)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 open fun refreshDir(dirDetect: DirDetect) {
 if (dirDetect.itemList.isEmpty()) { // ，
 deleteDir(dirDetect)
 } else {
 updateDir(dirDetect) // 
 val oldItemList: ArrayList<ItemDetect> = ArrayList(queryItemList(dirDetect.id))
 for (i in dirDetect.itemList.indices) {
 val item = dirDetect.itemList[i]
 item.position = i
 if (item.id == 0L) { // 
 item.id = insertItem(item)
 } else {
 updateItem(item)
 oldItemList.remove(item)
 }
 }
 for (delItem in oldItemList) {
 deleteItem(delItem)
 }
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @Transaction
 open fun copyDetect(oldDetect: HouseDetect): HouseDetect {
 val newDetect = oldDetect.copyOne()
 newDetect.id = insertDetect(newDetect)
 val dirList: List<DirDetect> = queryDirList(oldDetect.id)
 for (dir in dirList) {
 val itemList: List<ItemDetect> = queryItemList(dir.id)
 dir.id = 0
 dir.parentId = newDetect.id
 val dirId: Long = insertDir(dir)
 for (item in itemList) {
 item.id = 0
 item.parentId = dirId
 insertItem(item)
 }
 }
 return newDetect
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @Transaction
 open fun copyDir(
 dirList: ArrayList<DirDetect>,
 position: Int,
 ): DirDetect {
 // Comment removed (contained Chinese characters)
 for (i in position + 1 until dirList.size) {
 val dir: DirDetect = dirList[i]
 dir.position += 1
 updateDir(dir)
 }

 // Comment removed (contained Chinese characters)
 val oldDir = dirList[position]
 val newDir = oldDir.copyOne()
 newDir.id = insertDir(newDir)

 // Comment removed (contained Chinese characters)
 for (item in newDir.itemList) {
 item.parentId = newDir.id
 item.id = insertItem(item)
 item.dirDetect = newDir
 }
 return newDir
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @Transaction
 open fun copyItem(
 itemList: ArrayList<ItemDetect>,
 position: Int,
 ): ItemDetect {
 // Comment removed (contained Chinese characters)
 for (i in position + 1 until itemList.size) {
 val item: ItemDetect = itemList[i]
 item.position += 1
 updateItem(item)
 }

 // Comment removed (contained Chinese characters)
 val oldItem = itemList[position]
 val newItem = oldItem.copyOne(position = oldItem.position + 1, itemName = oldItem.copyName())
 newItem.id = insertItem(newItem)

 // Comment removed (contained Chinese characters)
 if (newItem.state > 0) {
 val dir = newItem.dirDetect
 when (newItem.state) {
 1 -> dir.goodCount++
 2 -> dir.warnCount++
 3 -> dir.dangerCount++
 }
 updateDir(dir)
 }
 return newItem
 }

 @Insert
 abstract fun insertDetect(houseDetect: HouseDetect): Long

 @Insert
 abstract fun insertDir(dirDetect: DirDetect): Long

 @Insert
 abstract fun insertItem(itemDetect: ItemDetect): Long

 @Delete
 abstract fun deleteDetect(vararg houseDetect: HouseDetect)

 @Delete
 abstract fun deleteDir(vararg dirDetect: DirDetect)

 @Delete
 abstract fun deleteItem(vararg itemDetect: ItemDetect)

 @Update
 abstract fun updateDetect(vararg houseDetect: HouseDetect)

 @Update
 abstract fun updateDir(vararg dirDetect: DirDetect)

 @Update
 abstract fun updateItem(vararg itemDetect: ItemDetect)

 /**
 * Comment removed (contained Chinese characters)
 */
 @Query("SELECT * FROM HouseDetect ORDER BY createTime DESC")
 abstract fun queryAll(): List<HouseDetect>

 @Query("SELECT * FROM HouseDetect WHERE id = :id")
 abstract fun queryDetectById(id: Long): HouseDetect?

 @Query("SELECT * FROM DirDetect WHERE id = :id")
 abstract fun queryDirById(id: Long): DirDetect?

 @Query("SELECT * FROM DirDetect WHERE parentId = :detectId ORDER BY position")
 abstract fun queryDirList(detectId: Long): List<DirDetect>

 @Query("SELECT * FROM ItemDetect WHERE parentId = :dirId ORDER BY position")
 abstract fun queryItemList(dirId: Long): List<ItemDetect>
}
