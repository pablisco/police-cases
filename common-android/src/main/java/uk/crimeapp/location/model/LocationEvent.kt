package uk.crimeapp.location.model

sealed class LocationEvent {

    object Load: LocationEvent()
    object Reload: LocationEvent()
    data class Select(val location: Location): LocationEvent()

}