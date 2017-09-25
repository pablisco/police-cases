package uk.crimeapp.common.android

import android.content.Context
import android.location.LocationManager

fun Context.locationManager(): LocationManager =
    getSystemService(Context.LOCATION_SERVICE) as LocationManager