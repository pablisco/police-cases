package uk.crimeapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import uk.crimeapp.common.android.start
import uk.crimeapp.location.LocationActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start<LocationActivity>()
        finish()
    }

}
