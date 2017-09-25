package uk.crimeapp.common.di

import okhttp3.OkHttpClient

class DataModule {

    val httpClient = OkHttpClient.Builder()
        .build()



}