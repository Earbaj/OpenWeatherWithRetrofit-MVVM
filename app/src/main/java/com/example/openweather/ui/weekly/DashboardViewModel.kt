package com.example.openweather.ui.weekly

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.openweather.model.WeatherDetail
import com.example.openweather.model.WeatherResponse
import com.example.openweather.repository.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DashboardViewModel : ViewModel() {

    private val _weatherForecast = MutableLiveData<List<WeatherDetail>>()
    val weatherForecast: LiveData<List<WeatherDetail>> = _weatherForecast

    fun fetchWeatherForecast(latitude: Double, longitude: Double, count: Int, apiKey: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getWeatherForecast(latitude, longitude, count, apiKey)
                _weatherForecast.postValue(response.list)
            } catch (e: IOException) {
                // Handle network error
                e.printStackTrace()
            } catch (e: HttpException) {
                // Handle HTTP error
                e.printStackTrace()
            }
        }
    }
}