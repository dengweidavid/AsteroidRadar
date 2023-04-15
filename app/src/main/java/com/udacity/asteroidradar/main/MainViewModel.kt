package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        viewModelScope.launch {
            refreshPictureOfDay()
            asteroidRepository.refreshAsteroids()
            _asteroids.value  = asteroidRepository.allAsteroids
        }
    }

    private val _asteroids = MutableLiveData<LiveData<List<Asteroid>>>()
    val asteroids: LiveData<LiveData<List<Asteroid>>>
        get() = _asteroids

    private suspend fun refreshPictureOfDay() {
        _pictureOfDay.value = AsteroidApi.retrofitService.getPictureOfDay()
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun onViewWeekAsteroidsClicked() {
        _asteroids.value = asteroidRepository.weekAsteroids
    }
    fun onTodayAsteroidsClicked() {
        _asteroids.value  = asteroidRepository.todayAsteroids
    }
    fun onSavedAsteroidsClicked() {
        _asteroids.value  = asteroidRepository.allAsteroids
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}