package com.ing.latlongtoaddress

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import java.io.IOException
import java.util.*

class LocationUtils(private val context: Context) {

    fun getAddressFromLocation(
        latitude: Double,
        longitude: Double,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val addressText = address.getAddressLine(0)
                onSuccess.invoke(addressText)
            } else {
                onError.invoke("Belirtilen konum için adres bulunamadı.")
            }
        } catch (ioException: IOException) {
            onError.invoke("Geocoder servisi kullanılamıyor.")
        } catch (exception: Exception) {
            onError.invoke("Adres alınamadı.")
        }
    }
}