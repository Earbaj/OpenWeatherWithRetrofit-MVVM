package com.example.openweather.ui.weekly.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.openweather.R
import com.example.openweather.model.WeatherDetail

class WeatherAdapter(context: Context, private val weatherList: List<WeatherDetail>) :
    ArrayAdapter<WeatherDetail>(context, 0, weatherList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_weather, parent, false)
        }

        val currentItem = weatherList[position]

        val textViewTemperature: TextView = itemView!!.findViewById(R.id.textViewTemperature)
        val imageViewWeatherIcon: ImageView = itemView.findViewById(R.id.imageViewWeatherIcon)

        textViewTemperature.text = "${currentItem.temp}°C"
        imageViewWeatherIcon.setImageResource(getWeatherIcon(currentItem.weather[0].icon))

        return itemView
    }

    private fun getWeatherIcon(iconCode: String): Int {
        // Add your logic to map weather icon code to appropriate drawable resource
        // For example:
        return when (iconCode) {
            "01d" -> R.drawable.default_day // Example: Clear sky (day)
            "02d" -> R.drawable.ic_clear_sky // Example: Few clouds (day)
            "03d" -> R.drawable.ic_scattered_cloud // Example: Scattered clouds (day)
            "04d" -> R.drawable.ic_clear_sky // Example: Broken clouds (day)
            // Add more cases for other weather conditions as needed
            else -> R.drawable.ic_clear_sky // Default icon if no match is found
        }
    }
}
