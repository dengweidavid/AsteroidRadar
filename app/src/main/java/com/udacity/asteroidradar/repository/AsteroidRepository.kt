@file:Suppress("BlockingMethodInNonBlockingContext")

package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    suspend fun refreshAsteroids(
        startDate: String = getToday(),
        endDate: String = getSeventhDay()
    ) {
        withContext(Dispatchers.IO) {
            try {
                val asteroids = AsteroidApi.retrofitService.getAsteroids(startDate, endDate)
                val result = parseAsteroidsJsonResult(JSONObject(asteroids))
                database.asteroidDao.insertAll(*result.asDatabaseModel())
                Log.d("Refresh Asteroids", "Success")
            } catch (err: Exception) {
                Log.e("Refresh Asteroids", err.message.toString())
            }
        }
    }

    suspend fun deletePreviousDayAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                database.asteroidDao.deletePreviousDayAsteroids(getToday())
                Log.d("Delete Asteroids", "Success")
            } catch (err: Exception) {
                Log.e("Delete Asteroids", err.message.toString())
            }

        }
    }
}