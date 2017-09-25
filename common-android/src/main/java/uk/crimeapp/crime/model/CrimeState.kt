package uk.crimeapp.crime.model

sealed class CrimeState {

    object Loading: CrimeState()
    object RequireGeoLocation: CrimeState()
    sealed class Loaded(open val crimes: List<Crime>): CrimeState() {
        data class WithSlug(override val crimes: List<Crime>, val searchSlug: String): Loaded(crimes)
        data class WithCoordinates(override val crimes: List<Crime>, val coordinates: CrimeCoordinates): Loaded(crimes)
    }
    object Empty: CrimeState()
    object Error: CrimeState()

}