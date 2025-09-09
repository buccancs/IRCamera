package com.topdon.lib.core.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.blankj.utilcode.util.TimeUtils

/**
 * [CN_TEXT] - [CN_TEXT].
 *
 * Created by LCG on 2024/1/15.
 */
open class HouseBase {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
     * [CN_TEXT]
     */
    @ColumnInfo
    var name: String = ""

    /**
     * [CN_TEXT]
     */
    @ColumnInfo
    var inspectorName: String = ""

    /**
     * [CN_TEXT]
     */
    @ColumnInfo
    var address: String = ""

    /**
     * [CN_TEXT]
     */
    @ColumnInfo
    var imagePath: String = ""

    /**
     * [CN_TEXT]
     */
    @ColumnInfo
    var year: Int? = null

    /**
     * [CN_TEXT].
     */
    @ColumnInfo
    var houseSpace: String = ""

    /**
     * [CN_TEXT] 0-[CN_TEXT] 1-[CN_TEXT] 2-[CN_TEXT]
     */
    @ColumnInfo
    var houseSpaceUnit: Int = 0

    /**
     * [CN_TEXT]
     */
    @ColumnInfo
    var cost: String = ""

    /**
     * [CN_TEXT]，0-[CN_TEXT]USD 1-[CN_TEXT]EUR 2-[CN_TEXT]GBP 3-[CN_TEXT]AUD 4-[CN_TEXT]JPY 5-[CN_TEXT]CAD 6-[CN_TEXT]NZD 7-Person[CN_TEXT]RMB 8-[CN_TEXT]HKD
     */
    @ColumnInfo
    var costUnit: Int = 0

    /**
     * [CN_TEXT]“[CN_TEXT]”[CN_TEXT]，[CN_TEXT]
     */
    @ColumnInfo
    var detectTime: Long = 0

    /**
     * [CN_TEXT]，[CN_TEXT]
     */
    @ColumnInfo
    var createTime: Long = 0

    /**
     * [CN_TEXT]，[CN_TEXT]
     */
    @ColumnInfo
    var updateTime: Long = 0

    override fun equals(other: Any?): Boolean = other is HouseBase && other.id == id

    override fun hashCode(): Int = id.toInt()

    /**
     * [CN_TEXT].
     */
    fun getSpaceUnitStr(): String =
        when (houseSpaceUnit) {
            0 -> "ac"
            1 -> "m²"
            else -> "ha"
        }

    /**
     * [CN_TEXT].
     */
    fun getCostUnitStr(): String =
        when (costUnit) {
            1 -> "EUR" // [CN_TEXT]EUR
            2 -> "GBP" // [CN_TEXT]GBP
            3 -> "AUD" // [CN_TEXT]AUD
            4 -> "JPY" // [CN_TEXT]JPY
            5 -> "CAD" // [CN_TEXT]CAD
            6 -> "NZD" // [CN_TEXT]NZD
            7 -> "RMB" // Person[CN_TEXT]RMB
            8 -> "HKD" // [CN_TEXT]HKD
            else -> "USD" // [CN_TEXT]USD
        }

    /**
     * [CN_TEXT] PDF [CN_TEXT]
     */
    fun getPdfFileName(): String = "TC_${TimeUtils.millis2String(createTime, "yyyyMMdd_HHmmss")}.pdf"
}

/**
 * [CN_TEXT] - [CN_TEXT].
 */
@Entity
class HouseDetect : HouseBase() {
    /**
     * [CN_TEXT]
     */
    @Ignore
    var dirList: ArrayList<DirDetect> = ArrayList()

    /**
     * [CN_TEXT] id [CN_TEXT] 0，[CN_TEXT] (1)，[CN_TEXT].
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
 * [CN_TEXT] - [CN_TEXT].
 */
@Entity
class HouseReport : HouseBase() {
    /**
     * [CN_TEXT]（[CN_TEXT]）[CN_TEXT]
     */
    @ColumnInfo
    var inspectorWhitePath: String = ""

    /**
     * [CN_TEXT]（[CN_TEXT]）[CN_TEXT]
     */
    @ColumnInfo
    var inspectorBlackPath: String = ""

    /**
     * [CN_TEXT]（[CN_TEXT]）[CN_TEXT]
     */
    @ColumnInfo
    var houseOwnerWhitePath: String = ""

    /**
     * [CN_TEXT]（[CN_TEXT]）[CN_TEXT]
     */
    @ColumnInfo
    var houseOwnerBlackPath: String = ""

    /**
     * [CN_TEXT]
     */
    @Ignore
    var dirList: ArrayList<DirReport> = ArrayList()
}
