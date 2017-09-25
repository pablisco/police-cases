package uk.crimeapp.crime

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager.GPS_PROVIDER
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_crime.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import uk.crimeapp.common.android.locationManager
import uk.crimeapp.common.android.visibleWhen
import uk.crimeapp.common.di.graph
import uk.crimeapp.crime.model.Crime
import uk.crimeapp.crime.model.CrimeCoordinates
import uk.crimeapp.crime.model.CrimeEvent
import uk.crimeapp.crime.model.CrimeState
import uk.crimeapp.main.R

class CrimeActivity : AppCompatActivity() {

    companion object {
        val LOCATION_NAME = "LOCATION_NAME"
        val LOCATION_REQUEST_CODE = 1
    }

    private val events by graph { crimeModule.events }
    private val states by graph { crimeModule.states }

    private val stateViews by lazy { listOf(loading, list, error, empty) }

    private var job: Job? = null

    private inline val slug get() = intent.data.host

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = intent.locationName
            setDisplayHomeAsUpEnabled(true)
        }
        val layoutManager = LinearLayoutManager(this)
        list.layoutManager = layoutManager
    }

    override fun onStart() {
        super.onStart()
        job = launch(CommonPool) {
            while (isActive) {
                waitForNextState()
            }
        }
        events.offer(CrimeEvent.Load(slug))
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }

    private suspend fun waitForNextState() {
        val state = states.receive()
        launch(UI) {
            when (state) {
                is CrimeState.Loading -> onLoading()
                is CrimeState.Loaded -> onLoaded(state.crimes)
                is CrimeState.Error -> onError()
                is CrimeState.Empty -> onEmptyResults()
                is CrimeState.RequireGeoLocation -> onGeoLocationRequired()
            }
        }
    }

    private fun onGeoLocationRequired() {
        onLoading()
        if (checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            val manager = locationManager()
            val location = manager.getLastKnownLocation(GPS_PROVIDER)

            if (location != null) {
                reportGeoLocation(location)
            } else {
                manager.requestSingleUpdate(GPS_PROVIDER, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            reportGeoLocation(location)
                            manager.removeUpdates(this)
                        }
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) = Unit

                    override fun onProviderEnabled(p0: String?) = Unit

                    override fun onProviderDisabled(p0: String?) = Unit
                }, mainLooper)
            }
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }
    }

    private fun reportGeoLocation(location: Location) {
        events.offer(CrimeEvent.LoadWithCoords(location.asCrimeCoordinate()))
    }

    private fun Location.asCrimeCoordinate(): CrimeCoordinates =
        CrimeCoordinates(latitude, longitude)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                    onGeoLocationRequired()
                } else {
                    // TODO more graceful error message
                    Toast.makeText(this, "Need location.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

    }

    private fun onEmptyResults() {
        stateViews.visibleWhen { it == empty }
    }

    private fun onError() {
        stateViews.visibleWhen { it == error }
    }

    private fun onLoaded(crimes: List<Crime>) {
        stateViews.visibleWhen { it == list }
        list.adapter = CrimeAdapter(crimes)
    }

    private fun onLoading() {
        stateViews.visibleWhen { it == loading }
    }

    @Suppress("UNUSED_PARAMETER")
    fun retry(ignored: View) {
        events.offer(CrimeEvent.Reload(slug))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

inline var Intent.locationName : String
    get() = getStringExtra(CrimeActivity.LOCATION_NAME)
    set(value) { putExtra(CrimeActivity.LOCATION_NAME, value) }
