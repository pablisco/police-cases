package uk.crimeapp

import uk.crimeapp.common.di.*
import uk.crimeapp.crime.InternalCrimeModule
import uk.crimeapp.location.InternalLocationModule

class AppGraph : Graph {

    private val dataModule = DataModule()
    override val navigationModule: NavigationModule = InternalNavigationModule()
    override val locationModule: LocationModule = InternalLocationModule(dataModule)
    override val crimeModule: CrimeModule = InternalCrimeModule(dataModule)

}