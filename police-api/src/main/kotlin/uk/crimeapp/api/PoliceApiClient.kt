package uk.crimeapp.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PoliceApiClient(
    httpClient: OkHttpClient = OkHttpClient(),
    baseUrl: String = "https://data.police.uk/api/"
) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    val forceService: ForceService
        get() = retrofit.create()

    val crimeService: CrimeService
        get() = retrofit.create()

    private inline fun <reified T> Retrofit.create() = create(T::class.java)

}