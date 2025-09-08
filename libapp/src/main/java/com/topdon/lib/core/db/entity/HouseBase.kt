package com.topdon.lib.core.db.entity

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.Utils
import com.csl.irCamera.libapp.R

 *  .
/**
 * @author LCG
 * @since Unknown
 */
open class HouseBase {
    @PrimaryKey(autoGenerate = true)
    /** id property */
    var id: Long = 0

    @ColumnInfo
    /** name property */
    var name: String = ""

    @ColumnInfo
    /** inspectorName property */
    var inspectorName: String = ""

    @ColumnInfo
    /** address property */
    var address: String = ""

    @ColumnInfo
    /** imagePath property */
    var imagePath: String = ""

    @ColumnInfo
    /** year property */
    var year: Int? = null

     * .
    @ColumnInfo
    /** houseSpace property */
    var houseSpace: String = ""

     *  0- 1- 2
    @ColumnInfo
    /** houseSpaceUnit property */
    var houseSpaceUnit: Int = 0

    @ColumnInfo
    /** cost property */
    var cost: String = ""

     * 0-USD 1-EUR 2-GBP 3-AUD 4-JPY 5-CAD 6-NZD 7-RMB 8-HKD
    @ColumnInfo
    /** costUnit property */
    var costUnit: Int = 0

     * “”
    @ColumnInfo
    /** detectTime property */
    var detectTime: Long = 0

    @ColumnInfo
    /** createTime property */
    var createTime: Long = 0

    @ColumnInfo
    /** updateTime property */
    var updateTime: Long = 0




    override fun equals(other: Any?): Boolean = other is HouseBase && other.id == id

    override fun hashCode(): Int = id.toInt()


     * .
    /**
     * Function description.
     */
    fun getSpaceUnitStr(): String = when (houseSpaceUnit) {
        0 -> "ac"
        1 -> "m²"
        else -> "ha"
    }

     * .
    /**
     * Function description.
     */
    fun getCostUnitStr(): String = when (costUnit) {
        1 -> "EUR" //EUR
        2 -> "GBP" //GBP
        3 -> "AUD" //AUD
        4 -> "JPY" //JPY
        5 -> "CAD" //CAD
        6 -> "NZD" //NZD
        7 -> "RMB" //RMB
        8 -> "HKD" //HKD
        else -> "USD" //USD
    }

     *  PDF
    /**
     * Function description.
     */
    fun getPdfFileName(): String = "TC_${TimeUtils.millis2String(createTime, "yyyyMMdd_HHmmss")}.pdf"
}



 *  .
@Entity
class HouseDetect : HouseBase() {
    @Ignore
    /** dirList property */
    var dirList: ArrayList<DirDetect> = ArrayList()

     *  id  0 (1).
    /**
     * Function description.
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

    /**
     * Function description.
     */
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



 *  .
@Entity
class HouseReport : HouseBase() {
    @ColumnInfo
    /** inspectorWhitePath property */
    var inspectorWhitePath: String = ""
    @ColumnInfo
    /** inspectorBlackPath property */
    var inspectorBlackPath: String = ""


    @ColumnInfo
    /** houseOwnerWhitePath property */
    var houseOwnerWhitePath: String = ""
    @ColumnInfo
    /** houseOwnerBlackPath property */
    var houseOwnerBlackPath: String = ""



    @Ignore
    /** dirList property */
    var dirList: ArrayList<DirReport> = ArrayList()
}