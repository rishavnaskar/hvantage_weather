package com.rishav.hvantage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val uiSettings = mMap.uiSettings

        uiSettings.isZoomControlsEnabled = true
        uiSettings.isMyLocationButtonEnabled = true
        uiSettings.isCompassEnabled = true
        uiSettings.isScrollGesturesEnabled = true
        uiSettings.isRotateGesturesEnabled = true
        uiSettings.isZoomGesturesEnabled = true

        val mapLoading: ProgressBar = findViewById(R.id.mapLoading)
        val currentLocationButton: ImageButton = findViewById(R.id.currentLocationButton)
        val searchText: EditText = findViewById(R.id.mapSearchText)
        val getWeatherButton: Button = findViewById(R.id.getWeatherButton)

        fetchLocation(mapLoading, getWeatherButton)
        getSearchData(searchText, getWeatherButton)

        currentLocationButton.setOnClickListener { fetchLocation(mapLoading, getWeatherButton) }
    }

    private fun getSearchData(searchText: EditText, weatherButton: Button) {
        searchText.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                geoLocate(searchText, weatherButton)
            }
            false
        }
    }

    private fun geoLocate(searchText: EditText, weatherButton: Button) {
        val searchString: String = searchText.text.toString()

        val geocoder: Geocoder = Geocoder(this)
        var list: List<Address> = ArrayList()
        try {
            list = geocoder.getFromLocationName(searchString, 1)
        } catch (e: Exception) {
            println("Error in geocoding - ${e.message}")
        }

        if (list.isNotEmpty()) {
            val address: Address = list[0]
            moveCamera(LatLng(address.latitude, address.longitude), 16F)
            navigateToWeather(weatherButton, address.latitude, address.longitude)
        }
    }

    private fun fetchLocation(mapLoading: ProgressBar, weatherButton: Button) {

        val task: Task<Location> = fusedLocationClient.lastLocation

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        task.addOnSuccessListener {
            if (it != null) {
                mapLoading.visibility = View.INVISIBLE
                val currentLocation = LatLng(it.latitude, it.longitude)
                moveCamera(currentLocation, 16.0f)

                navigateToWeather(weatherButton, it.latitude, it.longitude)
            } else {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }
    }

    private fun moveCamera(latLng: LatLng, zoom: Float) {
        mMap.clear()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        mMap.addMarker(MarkerOptions().position(latLng).title("Current Location"))
    }

    private fun navigateToWeather(weatherButton: Button, lat: Double, long: Double) {
        weatherButton.setOnClickListener {
            val intent: Intent = Intent(this, WeatherActivity::class.java)
            intent.putExtra("lat", lat)
            intent.putExtra("long", long)
            startActivity(intent)
        }
    }
}