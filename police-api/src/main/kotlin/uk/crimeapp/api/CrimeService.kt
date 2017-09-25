package uk.crimeapp.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CrimeService {

    @GET("crimes-no-location")
    fun findAllBy(
        @Query("force") forceId: String
    ): Call<List<Crime>>

    @GET("crimes-at-location")
    fun findAllBy(@Query("lat") latitude: Double, @Query("lng") longitude: Double): Call<List<Crime>>

    @GET("crime-categories")
    fun finAllCategories(): Call<List<CrimeCategory>>
}

data class Crime(
    val persistent_id: String,
    val category: String,
    val outcome_status: OutcomeStatus?,
    val month: String? = null,
    val context: String? = null,
    val location: CrimeLocation? = null
)

data class CrimeCategory(
    val name: String,
    val url: String
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

data class OutcomeStatus(
    val category: String
)