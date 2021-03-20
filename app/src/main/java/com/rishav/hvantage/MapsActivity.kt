package com.rishav.hvantage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.RequiresApi
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
import java.lang.Exception
import com.rishav.hvantage.MapsActivity as MapsActivity1


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.M)
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

        fetchLocation(mapLoading)
        getSearchData(searchText)

        currentLocationButton.setOnClickListener { fetchLocation(mapLoading) }
    }

    private fun getSearchData(searchText: EditText) {
        searchText.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                geoLocate(searchText)
            }
            false
        }
    }

    private fun geoLocate(searchText: EditText) {
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
        }
    }

    private fun fetchLocation(mapLoading: ProgressBar) {
        mapLoading.visibility = View.VISIBLE
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
            } else {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }
    }

    private fun moveCamera(latLng: LatLng, zoom: Float) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        mMap.addMarker(MarkerOptions().position(latLng).title("Current Location"))
    }
}