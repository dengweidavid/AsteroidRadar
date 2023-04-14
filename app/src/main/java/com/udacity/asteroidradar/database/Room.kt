package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM databaseAsteroid ORDER BY closeApproachDate ASC")
    fun getAllAsteroids(): LiveData<List<Asteroid>>

    @Query("SELECT * FROM databaseAsteroid WHERE closeApproachDate >= :startDate AND closeApproachDate <= :endDate ORDER BY closeApproachDate ASC")
    fun getAsteroidsByCloseApproachDate(startDate: String, endDate: String): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("DELETE FROM databaseAsteroid WHERE closeApproachDate < :today")
    fun deletePreviousDayAsteroids(today: String): Int
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    if (!::INSTANCE.isInitialized) {
        INSTANCE = Room.databaseBuilder(context.applicationContext,
            AsteroidDatabase::class.java,
            Constants.DATABASE_NAME).build()
    }
    return INSTANCE
}