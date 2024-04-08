package com.example.openweather.ui.today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.openweather.R
import com.example.openweather.databinding.FragmentHomeBinding
import com.example.openweather.model.WeatherResponse

class HomeFragment : Fragment() {

    private lateinit var weatherViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        weatherViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Observe weather forecast LiveData
        weatherViewModel.weatherForecast.observe(viewLifecycleOwner, { weatherResponse ->
            // Update UI with weather forecast data
            updateUI(weatherResponse)
        })

        // Fetch weather forecast data
        fetchWeatherForecast()
    }

    private fun fetchWeatherForecast() {
        // Replace these values with your desired latitude, longitude, and count
        val latitude = 25.3364
        val longitude = 89.2762
        val count = 1
        val apiKey = "4b61ba559f92a70dd75123c49b3bb707"

        weatherViewModel.fetchWeatherForecast(latitude, longitude, count, apiKey)
    }

    private fun updateUI(weatherResponse: WeatherResponse) {
        // Update UI elements with weather forecast data

        val temperatureInKelvin = weatherResponse.list[0].temp.day
        val temperatureInCelsius = temperatureInKelvin - 273.15
        val temperatureString = String.format("%.2f°C", temperatureInCelsius)

        binding.textViewCity.text = weatherResponse.city.name
        binding.textViewTemperature.text = temperatureString
        binding.textViewDescription.text = weatherResponse.list[0].weather[0].description

        // You can add more UI updates for other weather data here
    }


}