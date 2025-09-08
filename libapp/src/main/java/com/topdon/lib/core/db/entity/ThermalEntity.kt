package com.topdon.lib.core.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.topdon.lib.core.tools.TimeTool

@Entity(tableName = "thermal")
class ThermalEntity {

    @PrimaryKey(autoGenerate = true)
    /** id property */
    var id: Long = 0

    @ColumnInfo(name = "thermal_id")
    /** thermalId property */
    var thermalId: String = ""

    @ColumnInfo(name = "user_id")
    /** userId property */
    var userId: String = ""

    @ColumnInfo(name = "thermal")
    /** thermal property */
    var thermal: Float = 0f

    @ColumnInfo(name = "thermal_max")
    /** thermalMax property */
    var thermalMax: Float = 0f

    @ColumnInfo(name = "thermal_min")
    /** thermalMin property */
    var thermalMin: Float = 0f

    @ColumnInfo(name = "sn")
    /** sn property */
    var sn: String = ""

    @ColumnInfo(name = "info")
    /** info property */
    var info: String = ""

    @ColumnInfo(name = "type")
    /** type property */
    var type: String = ""

    @ColumnInfo(name = "start_time")
    /** startTime property */
    var startTime: Long = 0

    @ColumnInfo(name = "create_time")
    /** createTime property */
    var createTime: Long = 0

    //ms
    @ColumnInfo(name = "update_time")
    /** updateTime property */
    var updateTime: Long = 0

    override fun toString(): String {
        return "ThermalEntity(id=$id, thermalId='$thermalId', userId='$userId', thermal=$thermal, thermalMax=$thermalMax, thermalMin=$thermalMin, sn='$sn', info='$info', type='$type', startTime=$startTime, createTime=$createTime, updateTime=$updateTime)"
    }

    /**
     * Function description.
     */
    fun getTime(): String {
        return TimeTool.reportTime(createTime)
    }

    /**
     * Function description.
     */
    fun getMaxTemp(): Float {
        return thermalMax
    }

    /**
     * Function description.
     */
    fun getMinTemp(): Float {
        return thermalMin
    }

}
