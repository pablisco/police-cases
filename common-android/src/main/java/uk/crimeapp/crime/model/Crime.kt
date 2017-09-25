package uk.crimeapp.crime.model

import java.util.*

data class Crime(
    val id: String,
    val category: String,
    val outcomeStatus: String,
    val date: Date? = null,
    val context: String? = null,
    val location: CrimeLocation? = null
)

data class CrimeLocation(
    val latitude: Double,
    val longitude: Double,
    val street: CrimeLocationStreet
)

data class CrimeLocationStreet(
    val id: Long,
    val name: String
)
