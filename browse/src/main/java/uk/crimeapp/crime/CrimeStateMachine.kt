package uk.crimeapp.crime

import android.util.Log
import uk.crimeapp.common.AbstractStateMachine
import uk.crimeapp.crime.model.Crime
import uk.crimeapp.crime.model.CrimeCoordinates
import uk.crimeapp.crime.model.CrimeEvent
import uk.crimeapp.crime.model.CrimeState
import uk.crimeapp.crime.model.CrimeState.Empty
import uk.crimeapp.crime.model.CrimeState.Loaded
import uk.crimeapp.location.model.Location

class CrimeStateMachine(
    private val crimesFromSlug: suspend (String) -> List<Crime>,
    private val crimesFromCoords: suspend (CrimeCoordinates) -> List<Crime>
): AbstractStateMachine<CrimeEvent, CrimeState>() {

    private var state: CrimeState = CrimeState.Loading

    suspend override fun processEvent(event: CrimeEvent) = when(event) {
        is CrimeEvent.Load -> onLoad(event.locationSlug)
        is CrimeEvent.Reload -> onReload(event.locationSlug)
        is CrimeEvent.LoadWithCoords -> onLoad(event.coordinates)
    }

    private suspend fun onLoad(coordinates: CrimeCoordinates) {
        onLoad(
            requiresReload = { it !is CrimeState.Loaded.WithCoordinates || it.coordinates != coordinates },
            loadCrimes = { crimesFromCoords(coordinates) },
            createState = { Loaded.WithCoordinates(it, coordinates) }
        )
    }

    private suspend fun onLoad(locationSlug: String) {
        if (locationSlug == Location.NEAR_LOCATION.slug) {
            sendState(CrimeState.RequireGeoLocation)
        } else {
            onLoad(
                requiresReload = { it !is CrimeState.Loaded.WithSlug || it.searchSlug != locationSlug },
                loadCrimes = { crimesFromSlug(locationSlug) },
                createState = { Loaded.WithSlug(it, locationSlug) }
            )
        }
    }

    private suspend fun onLoad(
        requiresReload: (CrimeState) -> Boolean,
        loadCrimes: suspend () -> List<Crime>,
        createState: (List<Crime>) -> CrimeState
    ) {
        if (requiresReload(state)) {
            state = CrimeState.Loading
        }
        sendState(state)
        if (state == CrimeState.Loading) {
            state = try {
                val crimes = loadCrimes()
                if (crimes.isEmpty()) {
                    Empty
                } else {
                    createState(crimes)
                }
            } catch (e: Exception) {
                Log.d("Oops", "oops", e)
                // TODO inject logger
                CrimeState.Error
            }
            sendState(state)
        }
    }

    private suspend fun onReload(locationSlug: String) {
        state = CrimeState.Loading
        onLoad(locationSlug)
    }

}