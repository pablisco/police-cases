package uk.crimeapp.api

import retrofit2.Call
import retrofit2.http.GET

interface ForceService {

    @GET("forces")
    fun findAll() : Call<List<Force>>

}

data class Force(val id: String, val name: String)