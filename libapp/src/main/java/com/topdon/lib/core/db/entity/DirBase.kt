package com.topdon.lib.core.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.blankj.utilcode.util.Utils
import com.topdon.lib.core.R

/**
 * [CN_TEXT] [CN_TEXT] [CN_TEXT].
 *
 * Created by LCG on 2024/8/19.
 */
open class DirBase {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
     * [CN_TEXT] Id
     */
    @ColumnInfo(index = true)
    open var parentId: Long = 0

    /**
     * [CN_TEXT] index.
     */
    @ColumnInfo
    var position: Int = 0

    /**
     * [CN_TEXT]，[CN_TEXT]“[CN_TEXT]”
     */
    @ColumnInfo
    var dirName: String = ""

    /**
     * [CN_TEXT].
     */
    @ColumnInfo
    var goodCount: Int = 0

    /**
     * [CN_TEXT].
     */
    @ColumnInfo
    var warnCount: Int = 0

    /**
     * [CN_TEXT].
     */
    @ColumnInfo
    var dangerCount: Int = 0

    override fun equals(other: Any?): Boolean = other is DirBase && other.id == id

    override fun hashCode(): Int = id.toInt()

    fun getGoodCountStr(): String = if (goodCount > 99) "99+" else goodCount.toString()

    fun getWarnCountStr(): String = if (warnCount > 99) "99+" else warnCount.toString()

    fun getDangerCountStr(): String = if (dangerCount > 99) "99+" else dangerCount.toString()
}

/**
 * [CN_TEXT].
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = HouseDetect::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
)
class DirDetect() : DirBase() {
    @Ignore
    constructor(parentId: Long, position: Int, dirName: String) : this() {
        this.parentId = parentId
        this.position = position
        this.dirName = dirName
    }

    /**
     * [CN_TEXT] Id
     */
    @ColumnInfo(index = true)
    override var parentId: Long = 0

    /**
     * [CN_TEXT]Selected，[CN_TEXT].
     */
    @Ignore
    var hasSelect = false

    /**
     * [CN_TEXT]State
     */
    @Ignore
    var isExpand: Boolean = false

    /**
     * [CN_TEXT].
     */
    @Ignore
    var houseDetect = HouseDetect()

    /**
     * [CN_TEXT]
     */
    @Ignore
    var itemList: ArrayList<ItemDetect> = ArrayList()

    /**
     * [CN_TEXT] id [CN_TEXT] 0，[CN_TEXT] (1)，position + 1，itemList [CN_TEXT]，[CN_TEXT].
     */
    fun copyOne(): DirDetect {
        val newDirDetect = DirDetect()
        newDirDetect.id = 0
        newDirDetect.parentId = parentId
        newDirDetect.position = position + 1
        newDirDetect.dirName = "$dirName(1)"
        newDirDetect.goodCount = goodCount
        newDirDetect.warnCount = warnCount
        newDirDetect.dangerCount = dangerCount
        newDirDetect.isExpand = isExpand
        newDirDetect.hasSelect = hasSelect
        newDirDetect.houseDetect = houseDetect
        val newItemList: ArrayList<ItemDetect> = ArrayList(itemList.size)
        for (oldItem in itemList) {
            newItemList.add(oldItem.copyOne(parentId = 0))
        }
        newDirDetect.itemList = newItemList
        return newDirDetect
    }

    /**
     * [CN_TEXT]Current[CN_TEXT]，Note id、parent [CN_TEXT] 0，[CN_TEXT].
     */
    fun toDirReport(): DirReport {
        val dirReport = DirReport()
        dirReport.id = 0
        dirReport.parentId = 0
        dirReport.position = position
        dirReport.dirName = dirName
        dirReport.goodCount = goodCount
        dirReport.warnCount = warnCount
        dirReport.dangerCount = dangerCount

        val newItemList: ArrayList<ItemReport> = ArrayList(itemList.size)
        for (itemDetect in itemList) {
            if (itemDetect.state > 0 || itemDetect.inputText.isNotEmpty() || itemDetect.image1.isNotEmpty()) {
                newItemList.add(itemDetect.toItemReport())
            }
        }
        dirReport.itemList = newItemList
        return dirReport
    }

    companion object {
        /**
         * [CN_TEXT].
         */
        fun buildDefaultDirList(parentId: Long): ArrayList<DirDetect> =
            arrayListOf(
                DirDetect(parentId, 0, Utils.getApp().getString(R.string.detect_dir1_root)),
                DirDetect(parentId, 1, Utils.getApp().getString(R.string.detect_dir2_root)),
                DirDetect(parentId, 2, Utils.getApp().getString(R.string.detect_dir3_root)),
                DirDetect(parentId, 3, Utils.getApp().getString(R.string.detect_dir4_root)),
                DirDetect(parentId, 4, Utils.getApp().getString(R.string.detect_dir5_root)),
                DirDetect(parentId, 5, Utils.getApp().getString(R.string.detect_dir6_root)),
                DirDetect(parentId, 6, Utils.getApp().getString(R.string.detect_dir7_root)),
                DirDetect(parentId, 7, Utils.getApp().getString(R.string.detect_dir8_root)),
                DirDetect(parentId, 8, Utils.getApp().getString(R.string.detect_dir9_root)),
                DirDetect(parentId, 9, Utils.getApp().getString(R.string.detect_dir10_root)),
                DirDetect(parentId, 10, Utils.getApp().getString(R.string.detect_dir11_root)),
            )
    }
}

/**
 * [CN_TEXT].
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = HouseReport::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
)
class DirReport : DirBase() {
    /**
     * [CN_TEXT] Id
     */
    @ColumnInfo(index = true)
    override var parentId: Long = 0

    /**
     * [CN_TEXT]
     */
    @Ignore
    var itemList: ArrayList<ItemReport> = ArrayList()
}
