package com.ing.latlongtoaddress

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val locationPermissionRequestCode = 100
    private lateinit var locationUtils: LocationUtils

    private lateinit var latitudeEditText: EditText
    private lateinit var longitudeEditText: EditText
    private lateinit var getAddressButton: Button

    private lateinit var googleMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var addressTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationUtils = LocationUtils(this)

        latitudeEditText = findViewById(R.id.latitudeEditText)
        longitudeEditText = findViewById(R.id.longitudeEditText)
        getAddressButton = findViewById(R.id.getAddressButton)
        addressTextView = findViewById(R.id.addressTextView)


        mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.mapFragment, mapFragment)
                .commit()
        }
        mapFragment.getMapAsync(this)

        getAddressButton.setOnClickListener {
            val latitudeText = latitudeEditText.text.toString()
            val longitudeText = longitudeEditText.text.toString()

            if (latitudeText.isNotEmpty() && longitudeText.isNotEmpty()) {
                val latitude = latitudeText.toDouble()
                val longitude = longitudeText.toDouble()
                getAddress(latitude, longitude)
                addMarkerOnMap(latitude, longitude)
            } else {
                Toast.makeText(this, "Lütfen latitude ve longitude değerlerini girin.", Toast.LENGTH_SHORT).show()
            }
        }

        // Konum iznini kontrol et
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                locationPermissionRequestCode
            )
        }
        // Konum iznini kontrol et
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionRequestCode
            )
        }
    }


    private fun getAddress(latitude: Double, longitude: Double) {
        locationUtils.getAddressFromLocation(
            latitude,
            longitude,
            { address ->
                // Adres alımı başarılı
                runOnUiThread {
                    addressTextView.text = address
                    //Toast.makeText(this, "Adres: $address", Toast.LENGTH_SHORT).show()
                }
            },
            { errorMessage ->
                // Adres alımı başarısız
                runOnUiThread {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun addMarkerOnMap(latitude: Double, longitude: Double) {
        val markerOptions = MarkerOptions().position(LatLng(latitude, longitude))
        googleMap.addMarker(markerOptions)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 12f))
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.getUiSettings().setZoomControlsEnabled(true);

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Konum izni verildiğinde buraya girilir
            } else {
                Toast.makeText(this, "Konum izni reddedildi.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

