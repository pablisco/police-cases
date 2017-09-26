package uk.crimeapp.location.model

/**
 * Represents an available location to search crime.
 */
data class Location(val name: String, val slug: String) {
    companion object {
        val NO_LOCATION = Location("No location", "no-location")
        val NEAR_LOCATION = Location("Near me", "near")
    }
}