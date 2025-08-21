/*
 * Database functionality removed as requested to fix compilation issues
 * 
package com.topdon.lib.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.blankj.utilcode.util.Utils
// Removed house-related imports
// import com.topdon.lib.core.db.dao.HouseDetectDao
// import com.topdon.lib.core.db.dao.HouseReportDao
import com.topdon.lib.core.db.dao.ThermalDao
import com.topdon.lib.core.db.entity.*

@Database(
    entities = [
        ThermalEntity::class,
        // Removed house-related entities  
        // HouseDetect::class,
        // HouseReport::class,
        // DirDetect::class,
        // DirReport::class, 
        // ItemDetect::class,
        // ItemReport::class,
    ], version = 5
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun thermalDao(): ThermalDao

    // Removed house-related DAOs
    // abstract fun houseDetectDao(): HouseDetectDao
    // abstract fun houseReportDao(): HouseReportDao




    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context = Utils.getApp()): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "TopInfrared.db")
                .addMigrations(object : Migration(4, 5) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("DROP TABLE file")
                        database.execSQL("DROP TABLE tc001_file")
                        database.execSQL("DROP TABLE thermal_minute")
                        database.execSQL("DROP TABLE thermal_hour")
                        database.execSQL("DROP TABLE thermal_day")
                        database.execSQL("CREATE TABLE IF NOT EXISTS `thermal` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `thermal_id` TEXT NOT NULL, `user_id` TEXT NOT NULL, `thermal` REAL NOT NULL, `thermal_max` REAL NOT NULL, `thermal_min` REAL NOT NULL, `sn` TEXT NOT NULL, `info` TEXT NOT NULL, `type` TEXT NOT NULL, `start_time` INTEGER NOT NULL, `create_time` INTEGER NOT NULL, `update_time` INTEGER NOT NULL)")
                        // Removed house-related tables
                        // database.execSQL("CREATE TABLE IF NOT EXISTS `HouseDetect` ...")
                        // database.execSQL("CREATE TABLE IF NOT EXISTS `HouseReport` ...")  
                        // database.execSQL("CREATE TABLE IF NOT EXISTS `DirDetect` ...")
                        // database.execSQL("CREATE TABLE IF NOT EXISTS `DirReport` ...")
                        // database.execSQL("CREATE TABLE IF NOT EXISTS `ItemDetect` ...")
                        // database.execSQL("CREATE TABLE IF NOT EXISTS `ItemReport` ...")
                        // database.execSQL("CREATE INDEX IF NOT EXISTS `index_DirDetect_parentId` ON `DirDetect` (`parentId`)")
                        // database.execSQL("CREATE INDEX IF NOT EXISTS `index_DirReport_parentId` ON `DirReport` (`parentId`)")
                        // database.execSQL("CREATE INDEX IF NOT EXISTS `index_ItemDetect_parentId` ON `ItemDetect` (`parentId`)")
                        // database.execSQL("CREATE INDEX IF NOT EXISTS `index_ItemReport_parentId` ON `ItemReport` (`parentId`)")
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
    }
}
*/

// Database functionality completely removed - this file is now commented out