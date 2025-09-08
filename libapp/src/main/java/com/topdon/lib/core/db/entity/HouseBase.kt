package com.topdon.lib.core.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.blankj.utilcode.util.TimeUtils

/**
 * [Chinese text] - [Chinese text].
 *
 * Created by LCG on 2024/1/15.
 */
open class HouseBase {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
     * [Chinese text]
     */
    @ColumnInfo
    var name: String = ""

    /**
     * [Chinese text]
     */
    @ColumnInfo
    var inspectorName: String = ""

    /**
     * [Chinese text]
     */
    @ColumnInfo
    var address: String = ""

    /**
     * [Chinese text]
     */
    @ColumnInfo
    var imagePath: String = ""

    /**
     * [Chinese text]
     */
    @ColumnInfo
    var year: Int? = null

    /**
     * [Chinese text].
     */
    @ColumnInfo
    var houseSpace: String = ""

    /**
     * [Chinese text] 0-[Chinese text] 1-[Chinese text] 2-[Chinese text]
     */
    @ColumnInfo
    var houseSpaceUnit: Int = 0

    /**
     * [Chinese text]
     */
    @ColumnInfo
    var cost: String = ""

    /**
     * [Chinese text], 0-[Chinese text]USD 1-[Chinese text]EUR 2-[Chinese text]GBP 3-[Chinese text]AUD 4-[Chinese text]JPY 5-[Chinese text]CAD 6-[Chinese text]NZD 7-[Chinese text]RMB 8-[Chinese text]HKD
     */
    @ColumnInfo
    var costUnit: Int = 0

    /**
     * [Chinese text]"[Chinese text]"[Chinese text], [Chinese text]
     */
    @ColumnInfo
    var detectTime: Long = 0

    /**
     * [Chinese text], [Chinese text]
     */
    @ColumnInfo
    var createTime: Long = 0

    /**
     * [Chinese text], [Chinese text]
     */
    @ColumnInfo
    var updateTime: Long = 0

    override fun equals(other: Any?): Boolean = other is HouseBase && other.id == id

    override fun hashCode(): Int = id.toInt()

    /**
     * [Chinese text].
     */
    fun getSpaceUnitStr(): String =
        when (houseSpaceUnit) {
            0 -> "ac"
            1 -> "m^2"
            else -> "ha"
        }

    /**
     * [Chinese text].
     */
    fun getCostUnitStr(): String =
        when (costUnit) {
            1 -> "EUR" // [Chinese text]EUR
            2 -> "GBP" // [Chinese text]GBP
            3 -> "AUD" // [Chinese text]AUD
            4 -> "JPY" // [Chinese text]JPY
            5 -> "CAD" // [Chinese text]CAD
            6 -> "NZD" // [Chinese text]NZD
            7 -> "RMB" // [Chinese text]RMB
            8 -> "HKD" // [Chinese text]HKD
            else -> "USD" // [Chinese text]USD
        }

    /**
     * [Chinese text] PDF [Chinese text]
     */
    fun getPdfFileName(): String = "TC_${TimeUtils.millis2String(createTime, "yyyyMMdd_HHmmss")}.pdf"
}

/**
 * [Chinese text] - [Chinese text].
 */
@Entity
class HouseDetect : HouseBase() {
    /**
     * [Chinese text]
     */
    @Ignore
    var dirList: ArrayList<DirDetect> = ArrayList()

    /**
     * [Chinese text] id [Chinese text] 0, [Chinese text] (1), [Chinese text].
     */
    fun copyOne(): HouseDetect {
        val newDetect = HouseDetect()
        newDetect.id = 0
        newDetect.name = "$name(1)"
        newDetect.inspectorName = inspectorName
        newDetect.address = address
        newDetect.imagePath = imagePath
        newDetect.year = year
        newDetect.houseSpace = houseSpace
        newDetect.houseSpaceUnit = houseSpaceUnit
        newDetect.cost = cost
        newDetect.costUnit = costUnit
        newDetect.detectTime = detectTime
        newDetect.createTime = createTime
        newDetect.updateTime = updateTime
        return newDetect
    }

    fun toHouseReport(): HouseReport {
        val houseReport = HouseReport()
        houseReport.id = 0
        houseReport.name = name
        houseReport.inspectorName = inspectorName
        houseReport.address = address
        houseReport.imagePath = imagePath
        houseReport.year = year
        houseReport.houseSpace = houseSpace
        houseReport.houseSpaceUnit = houseSpaceUnit
        houseReport.cost = cost
        houseReport.costUnit = costUnit
        houseReport.detectTime = detectTime
        houseReport.createTime = createTime
        houseReport.updateTime = updateTime

        val newDirList: ArrayList<DirReport> = ArrayList(dirList.size)
        for (dirDetect in dirList) {
            if (dirDetect.itemList.isNotEmpty()) {
                val dirRepost: DirReport = dirDetect.toDirReport()
                if (dirRepost.itemList.isNotEmpty()) {
                    newDirList.add(dirRepost)
                }
            }
        }
        houseReport.dirList = newDirList
        return houseReport
    }
}

/**
 * [Chinese text] - [Chinese text].
 */
@Entity
class HouseReport : HouseBase() {
    /**
     * [Chinese text]([Chinese text])[Chinese text]
     */
    @ColumnInfo
    var inspectorWhitePath: String = ""

    /**
     * [Chinese text]([Chinese text])[Chinese text]
     */
    @ColumnInfo
    var inspectorBlackPath: String = ""

    /**
     * [Chinese text]([Chinese text])[Chinese text]
     */
    @ColumnInfo
    var houseOwnerWhitePath: String = ""

    /**
     * [Chinese text]([Chinese text])[Chinese text]
     */
    @ColumnInfo
    var houseOwnerBlackPath: String = ""

    /**
     * [Chinese text]
     */
    @Ignore
    var dirList: ArrayList<DirReport> = ArrayList()
}
