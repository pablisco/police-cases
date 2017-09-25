package uk.crimeapp.crime

import uk.crimeapp.api.CrimeCategory
import uk.crimeapp.api.PoliceApiClient
import uk.crimeapp.common.await
import uk.crimeapp.crime.model.Crime
import uk.crimeapp.crime.model.CrimeCoordinates
import uk.crimeapp.crime.model.CrimeLocation
import uk.crimeapp.crime.model.CrimeLocationStreet
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import uk.crimeapp.api.Crime as ApiCrime
import uk.crimeapp.api.CrimeLocation as ApiCrimeLocation
import uk.crimeapp.api.CrimeLocationStreet as ApiCrimeLocationStreet

class CrimeRepository(
    apiClient: PoliceApiClient
) {

    companion object {
        val DATE_FORMAT: DateFormat = SimpleDateFormat("yyyy-MM", Locale.ENGLISH)
    }

    private val crimeService = apiClient.crimeService

    suspend fun findAll(slug: String): List<Crime> {
        val categories = crimeService.finAllCategories().await()
        return crimeService.findAllBy(slug).await().map {
            it.asModel(categories)
        }
    }

    suspend fun findAll(c: CrimeCoordinates): List<Crime> {
        val categories = crimeService.finAllCategories().await()
        return crimeService.findAllBy(c.latitude, c.longiture).await().map {
            it.asModel(categories)
        }
    }

    private fun ApiCrime.asModel(categories: List<CrimeCategory>): Crime {
        val category = categories
            .filter { it.url == category }
            .map { it.name }
            .firstOrNull() ?: "Unknown"
        val date = DATE_FORMAT.parse(month)
        return Crime(persistent_id, category, outcome_status?.category ?: "Unknown", date, context, location?.asModel())
    }

    private fun ApiCrimeLocation.asModel() =
        CrimeLocation(latitude, longitude, street.asModel())

    private fun ApiCrimeLocationStreet.asModel() =
        CrimeLocationStreet(id, name)
}