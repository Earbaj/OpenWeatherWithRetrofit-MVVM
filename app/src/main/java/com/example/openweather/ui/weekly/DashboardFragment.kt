package com.example.openweather.ui.weekly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.openweather.R
import com.example.openweather.databinding.FragmentDashboardBinding
import com.example.openweather.ui.weekly.adapter.WeatherAdapter

class DashboardFragment : Fragment() {

    private lateinit var listViewWeather: ListView
    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        listViewWeather = view.findViewById(R.id.listViewWeather)
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        val adapter = WeatherAdapter(requireContext(), mutableListOf()) // Initialize with empty list
        listViewWeather.adapter = adapter

        observeViewModel(adapter)

        viewModel.fetchWeatherForecast(25.3365884,89.2801531,7,"4b61ba559f92a70dd75123c49b3bb707") // Fetch weather data

        return view
    }

    private fun observeViewModel(adapter: WeatherAdapter) {
        viewModel.weatherForecast.observe(viewLifecycleOwner, { weatherList ->
            adapter.clear()
            adapter.addAll(weatherList)
            adapter.notifyDataSetChanged()
        })
}
}