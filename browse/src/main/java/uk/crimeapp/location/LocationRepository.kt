package uk.crimeapp.location

import uk.crimeapp.api.PoliceApiClient
import uk.crimeapp.common.await
import uk.crimeapp.location.model.Location

class LocationRepository(
    private val apiClient: PoliceApiClient
) {

    suspend fun findAll(): List<Location> =
        apiClient.forceService.findAll().await().map { Location(it.name, it.id) }

}