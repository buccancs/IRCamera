package com.topdon.lib.core.db.dao

import androidx.room.*
import com.topdon.lib.core.db.entity.ThermalEntity

@Dao
interface ThermalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: ThermalEntity): Long

    @Query("SELECT type AS type, start_time AS startTime, count(*) AS duration FROM thermal GROUP BY start_time ORDER BY start_time DESC")
    fun queryRecordList(): List<Record>
    @Query("SELECT * FROM thermal WHERE start_time = :startTime ORDER BY create_time")
    fun queryDetail(startTime: Long): List<ThermalEntity>

    @Query("DELETE FROM thermal where start_time = :startTime")
    fun delDetail(startTime: Long)
    //删除用户数据
    @Query("delete from thermal where user_id = :userId")
    fun deleteByUserId(userId: String)

    //删除无用0数据
    @Query("delete from thermal where user_id = :userId and thermal=0 and thermal_max=0 and thermal_min=0 and create_time<(select max(create_time) from thermal where thermal=0 and thermal_max=0 and thermal_min=0)")
    fun deleteZero(userId: String)

    //根据时间范围查询数据
    @Query("SELECT * FROM thermal WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime ORDER BY create_time")
    fun getThermalByDate(userId: String, startTime: Long, endTime: Long): List<ThermalEntity>

    //根据时间范围查询数据(别名为queryByTime)
    @Query("SELECT * FROM thermal WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime ORDER BY create_time")
    fun queryByTime(userId: String, startTime: Long, endTime: Long): List<ThermalEntity>

    //查询用户所有数据
    @Query("SELECT * FROM thermal WHERE user_id = :userId ORDER BY create_time")
    fun getAllThermalByDate(userId: String): List<ThermalEntity>

    //查询时间范围内最大温度值
    @Query("SELECT MAX(thermal_max) FROM thermal WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime")
    fun queryByTimeMax(userId: String, startTime: Long, endTime: Long): Float?

    //查询时间范围内最小温度值
    @Query("SELECT MIN(thermal_min) FROM thermal WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime")
    fun queryByTimeMin(userId: String, startTime: Long, endTime: Long): Float?

    //查询最新记录时间
    @Query("SELECT MAX(create_time) FROM thermal WHERE user_id = :userId")
    fun queryMaxTime(userId: String): Long

    //根据时间范围和类型查询数据
    @Query("SELECT * FROM thermal WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime AND type = :type ORDER BY create_time")
    fun queryByTime(userId: String, startTime: Long, endTime: Long, type: String): List<ThermalEntity>

    //查询时间范围和类型内最大温度值
    @Query("SELECT MAX(thermal_max) FROM thermal WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime AND type = :type")
    fun queryByTimeMax(userId: String, startTime: Long, endTime: Long, type: String): Float?

    //查询时间范围和类型内最小温度值
    @Query("SELECT MIN(thermal_min) FROM thermal WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime AND type = :type")
    fun queryByTimeMin(userId: String, startTime: Long, endTime: Long, type: String): Float?

    data class Record(
        var type: String? = "point",    // point-点 line-线 fence-面
        var startTime: Long = 0, // 开始时刻时间戳，单位毫秒
        var duration: Int = 0,
        @Ignore
        var showTitle : Boolean = false
    )
}