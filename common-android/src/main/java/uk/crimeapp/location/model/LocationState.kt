package uk.crimeapp.location.model

sealed class LocationState {

    object Loading: LocationState()
    data class Loaded(val locations: List<Location>): LocationState()
    object Failure : LocationState()
    data class Navigate(val location: Location): LocationState()

}