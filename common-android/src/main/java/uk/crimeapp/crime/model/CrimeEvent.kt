package uk.crimeapp.crime.model

sealed class CrimeEvent {

    data class Load(val locationSlug: String): CrimeEvent()
    data class LoadWithCoords(val coordinates: CrimeCoordinates): CrimeEvent()
    data class Reload(val locationSlug: String): CrimeEvent()
    data class Selected(val crime: Crime): CrimeEvent()

}