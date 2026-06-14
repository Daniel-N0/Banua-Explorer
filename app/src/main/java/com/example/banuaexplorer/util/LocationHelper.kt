package com.example.banuaexplorer.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context,
    onResult: (Double, Double) -> Unit
) {

    val fusedClient =
        LocationServices.getFusedLocationProviderClient(context)

    fusedClient.lastLocation.addOnSuccessListener {

        if (it != null) {

            onResult(
                it.latitude,
                it.longitude
            )

        }

    }

}