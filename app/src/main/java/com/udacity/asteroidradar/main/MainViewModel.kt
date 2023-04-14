package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    init {
        viewModelScope.launch {
            refreshPictureOfDay()
        }
    }

    private suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            try {
                _pictureOfDay.postValue(
                    AsteroidApi.retrofitService.getPictureOfDay(API_KEY)
                )
            } catch (err: Exception) {
                Log.e("refreshPictureOfDay", err.printStackTrace().toString())
            }
        }
    }
}