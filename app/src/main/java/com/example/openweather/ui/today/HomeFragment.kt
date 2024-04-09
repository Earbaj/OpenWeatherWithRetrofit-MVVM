package com.example.openweather.ui.today

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.openweather.R
import com.example.openweather.databinding.FragmentHomeBinding
import com.example.openweather.model.WeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {

    // FusedLocationProviderClient instance to fetch location
    var client: FusedLocationProviderClient? = null

    // ViewModel for weather data
    private lateinit var weatherViewModel: HomeViewModel
    // Binding object for the fragment layout
    private lateinit var binding: FragmentHomeBinding

    // Latitude and Longitude variables to store user's location
    var latitude: Double = 0.0
    var longitude: Double = 0.0


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

        initCurrentLocation0()

        // Initialize ViewModel
        weatherViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Observe weather forecast LiveData
        weatherViewModel.weatherForecast.observe(viewLifecycleOwner, { weatherResponse ->
            // Update UI with weather forecast data
            updateUI(weatherResponse)
        })

    }

    private fun initCurrentLocation0() {
        // Initialize the FusedLocationProviderClient
        client = getActivity()?.let {
            LocationServices
                .getFusedLocationProviderClient(
                    it
                )
        }
        // Check if location permissions are granted
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            // When permission is granted
            // Call method
            getCurrentLocation()
        } else {
            // When permission is not granted
            // Call method
            requestPermissions(
                arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
        }
    }


    // Callback method for permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )
        // Check if permission request is for location and granted
        if (requestCode == 100 && grantResults.size > 0
            && (grantResults[0] + grantResults[1]
                    == PackageManager.PERMISSION_GRANTED)
        ) {
            // When permission are granted
            // Call  method
            getCurrentLocation()
        } else {
            // When permission are denied
            // Display toast
            Toast
                .makeText(
                    activity,
                    "Permission denied",
                    Toast.LENGTH_SHORT
                )
                .show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        // Initialize Location manager
        val locationManager = activity
            ?.getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager
        // Check condition
        if (locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )
            || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            // When location service is enabled
            // Get last location
            client!!.lastLocation.addOnCompleteListener(
                OnCompleteListener<Location?> { task ->
                    // Initialize location
                    val location: Location? = task.result
                    // Check condition
                    if (location != null) {
                        // When location result is not
                        // null set latitude
                        // Set latitude
                        latitude = location.getLatitude()
                        // Set longitude
                        longitude = location.getLongitude()
                        fetchWeatherForecast()
                    } else {
                        // When location result is null
                        // initialize location request
                        val locationRequest: LocationRequest = LocationRequest()
                            .setPriority(
                                LocationRequest.PRIORITY_HIGH_ACCURACY
                            )
                            .setInterval(10000)
                            .setFastestInterval(
                                1000
                            )
                            .setNumUpdates(1)

                        // Initialize location call back
                        val locationCallback: LocationCallback = object : LocationCallback() {
                            override fun onLocationResult(
                                locationResult: LocationResult
                            ) {
                                // Initialize
                                // location
                                val location1: Location = locationResult
                                    .lastLocation
                                // Set latitude
                                latitude = location1.getLatitude()
                                // Set longitude
                                longitude = location1.getLongitude()
                                fetchWeatherForecast()
                            }
                        }

                        // Request location updates
                        client!!.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.myLooper()
                        )
                    }
                })
        } else {
            // When location service is not enabled
            // open location setting
            startActivity(
                Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                    .setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )
            )
        }
    }


    private fun fetchWeatherForecast() {
        val count = 1
        val apiKey = "4b61ba559f92a70dd75123c49b3bb707"
        Log.d(TAG, "$latitude")
        Log.d(TAG, "$longitude")
        weatherViewModel.fetchWeatherForecast(latitude, longitude, count, apiKey)
    }


    private fun updateUI(weatherResponse: WeatherResponse) {
        // Update UI elements with weather forecast data

        val temperatureInKelvin = weatherResponse.list[0].temp.day
        val temperatureInCelsius = temperatureInKelvin - 273.15
        val temperatureString = String.format("%.2f°C", temperatureInCelsius)

        val timestamp = weatherResponse.list[0].dt // Assuming it's a Long value

        // Create a Date object from the timestamp
        val date = Date(timestamp * 1000)

        // Create a SimpleDateFormat object to specify the date format
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        // Format the date and time
        val formattedDateTime = sdf.format(date)

        val iconResource = when (weatherResponse.list[0].weather[0].icon) {
            "01d" -> R.drawable.default_day // Example: Clear sky (day)
            "02d" -> R.drawable.ic_clear_sky // Example: Few clouds (day)
            "03d" -> R.drawable.ic_scattered_cloud // Example: Scattered clouds (day)
            "04d" -> R.drawable.ic_clear_sky // Example: Broken clouds (day)
            // Add more cases for other weather conditions as needed
            else -> R.drawable.ic_clear_sky // Default icon if no match is found
        }

        binding.textViewCity.text = weatherResponse.city.name
        binding.textViewTemperature.text = temperatureString
        binding.textViewDescription.text = weatherResponse.list[0].weather[0].description
        binding.textViewDate.text = formattedDateTime
        binding.imageView.setImageResource(iconResource)


        // You can add more UI updates for other weather data here
    }


    companion object {
        private const val TAG = "HomeFragment"
    }


}

