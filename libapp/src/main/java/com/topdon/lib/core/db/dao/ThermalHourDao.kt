package com.topdon.lib.core.db.dao

import androidx.room.*
import com.topdon.lib.core.db.entity.ThermalHourEntity

@Dao
interface ThermalHourDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: ThermalHourEntity): Long

    @Query("SELECT * FROM thermal_hour WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime ORDER BY create_time")
    fun queryByTime(userId: String, startTime: Long, endTime: Long): List<ThermalHourEntity>

    @Query("SELECT * FROM thermal_hour WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime AND type = :type ORDER BY create_time")
    fun queryByTime(userId: String, startTime: Long, endTime: Long, type: String): List<ThermalHourEntity>

    @Query("SELECT MAX(thermal_max) FROM thermal_hour WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime")
    fun queryByTimeMax(userId: String, startTime: Long, endTime: Long): Float

    @Query("SELECT MIN(thermal_min) FROM thermal_hour WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime")
    fun queryByTimeMin(userId: String, startTime: Long, endTime: Long): Float

    @Query("SELECT MAX(create_time) FROM thermal_hour WHERE user_id = :userId")
    fun queryMaxTime(userId: String): Long

    @Query("DELETE FROM thermal_hour WHERE user_id = :userId AND id NOT IN (SELECT MAX(id) FROM thermal_hour WHERE user_id = :userId GROUP BY create_time)")
    fun deleteRepeatVol(userId: String)

    @Query("DELETE FROM thermal_hour WHERE user_id = :userId")
    fun deleteByUserId(userId: String)
}