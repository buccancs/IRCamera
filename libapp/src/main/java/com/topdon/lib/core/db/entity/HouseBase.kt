package com.topdon.lib.core.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.blankj.utilcode.util.TimeUtils

/**
 * data - data.
 *
 * Created by LCG on 2024/1/15.
 */
open class HouseBase {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
     * data
     */
    @ColumnInfo
    var name: String = ""

    /**
     * data
     */
    @ColumnInfo
    var inspectorName: String = ""

    /**
     * data
     */
    @ColumnInfo
    var address: String = ""

    /**
     * data
     */
    @ColumnInfo
    var imagePath: String = ""

    /**
     * data
     */
    @ColumnInfo
    var year: Int? = null

    /**
     * data.
     */
    @ColumnInfo
    var houseSpace: String = ""

    /**
     * data 0-data 1-data 2-data
     */
    @ColumnInfo
    var houseSpaceUnit: Int = 0

    /**
     * data
     */
    @ColumnInfo
    var cost: String = ""

    /**
     * data，0-dataUSD 1-dataEUR 2-dataGBP 3-dataAUD 4-dataJPY 5-dataCAD 6-dataNZD 7-PersondataRMB 8-dataHKD
     */
    @ColumnInfo
    var costUnit: Int = 0

    /**
     * data“data”data，data
     */
    @ColumnInfo
    var detectTime: Long = 0

    /**
     * data，data
     */
    @ColumnInfo
    var createTime: Long = 0

    /**
     * data，data
     */
    @ColumnInfo
    var updateTime: Long = 0

    override fun equals(other: Any?): Boolean = other is HouseBase && other.id == id

    override fun hashCode(): Int = id.toInt()

    /**
     * data.
     */
    fun getSpaceUnitStr(): String =
        when (houseSpaceUnit) {
            0 -> "ac"
            1 -> "m²"
            else -> "ha"
        }

    /**
     * data.
     */
    fun getCostUnitStr(): String =
        when (costUnit) {
            1 -> "EUR" // dataEUR
            2 -> "GBP" // dataGBP
            3 -> "AUD" // dataAUD
            4 -> "JPY" // dataJPY
            5 -> "CAD" // dataCAD
            6 -> "NZD" // dataNZD
            7 -> "RMB" // PersondataRMB
            8 -> "HKD" // dataHKD
            else -> "USD" // dataUSD
        }

    /**
     * data PDF data
     */
    fun getPdfFileName(): String = "TC_${TimeUtils.millis2String(createTime, "yyyyMMdd_HHmmss")}.pdf"
}

 */
@Entity
class HouseDetect : HouseBase() {
    /**
     * data
     */
    @Ignore
    var dirList: ArrayList<DirDetect> = ArrayList()

    /**
     * data id data 0，data (1)，data.
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

 */
@Entity
class HouseReport : HouseBase() {
    /**
     * data（data）data
     */
    @ColumnInfo
    var inspectorWhitePath: String = ""

    /**
     * data（data）data
     */
    @ColumnInfo
    var inspectorBlackPath: String = ""

    /**
     * data（data）data
     */
    @ColumnInfo
    var houseOwnerWhitePath: String = ""

    /**
     * data（data）data
     */
    @ColumnInfo
    var houseOwnerBlackPath: String = ""

    /**
     * data
     */
    @Ignore
    var dirList: ArrayList<DirReport> = ArrayList()
}
