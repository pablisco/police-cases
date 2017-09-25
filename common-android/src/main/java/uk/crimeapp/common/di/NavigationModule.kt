package uk.crimeapp.common.di

import android.content.Context
import android.content.Intent

interface NavigationModule {

    fun startActivity(context: Context, intent: Intent)

}

class InternalNavigationModule : NavigationModule {

    override fun startActivity(context: Context, intent: Intent) =
        context.startActivity(intent)

}