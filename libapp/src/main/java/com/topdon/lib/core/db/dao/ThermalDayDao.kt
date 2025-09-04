package com.topdon.lib.core.db.dao

import androidx.room.*
import com.topdon.lib.core.db.entity.ThermalDayEntity

@Dao
interface ThermalDayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: ThermalDayEntity): Long

    @Query("SELECT * FROM thermal_day WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime ORDER BY create_time")
    fun queryByTime(userId: String, startTime: Long, endTime: Long): List<ThermalDayEntity>

    @Query("SELECT * FROM thermal_day WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime AND type = :type ORDER BY create_time")
    fun queryByTime(userId: String, startTime: Long, endTime: Long, type: String): List<ThermalDayEntity>

    @Query("SELECT MAX(thermal_max) FROM thermal_day WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime")
    fun queryByTimeMax(userId: String, startTime: Long, endTime: Long): Float

    @Query("SELECT MIN(thermal_min) FROM thermal_day WHERE user_id = :userId AND create_time >= :startTime AND create_time <= :endTime")
    fun queryByTimeMin(userId: String, startTime: Long, endTime: Long): Float

    @Query("SELECT MAX(create_time) FROM thermal_day WHERE user_id = :userId")
    fun queryMaxTime(userId: String): Long

    @Query("DELETE FROM thermal_day WHERE user_id = :userId AND id NOT IN (SELECT MAX(id) FROM thermal_day WHERE user_id = :userId GROUP BY create_time)")
    fun deleteRepeatVol(userId: String)

    @Query("DELETE FROM thermal_day WHERE user_id = :userId")
    fun deleteByUserId(userId: String)
}