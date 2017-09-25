package uk.crimeapp.location

import uk.crimeapp.common.AbstractStateMachine
import uk.crimeapp.location.model.Location
import uk.crimeapp.location.model.LocationEvent
import uk.crimeapp.location.model.LocationState
import java.lang.Exception

class LocationStateMachine(
    private val retrieveLocations: suspend () -> List<Location>
) : AbstractStateMachine<LocationEvent, LocationState>() {

    override suspend fun processEvent(event: LocationEvent) = when(event) {
        is LocationEvent.Load -> onLoad()
        is LocationEvent.Reload -> onReload()
        is LocationEvent.Select -> onSelect(event.location)
    }

    private var state: LocationState = LocationState.Loading

    private suspend fun onLoad() {
        if (state is LocationState.Failure) {
            state = LocationState.Loading
        }
        sendState(state)
        if (state !is LocationState.Loaded) {
            state = try {
                LocationState.Loaded(

                    listOf(Location.NEAR_LOCATION) + retrieveLocations()
                )
            } catch (e: Exception) {
                // TODO inject logger
                LocationState.Failure
            }
            sendState(state)
        }
    }

    private suspend fun onReload() {
        state = LocationState.Loading
        onLoad()
    }

    private suspend fun onSelect(location: Location) =
        states.send(LocationState.Navigate(location))

}