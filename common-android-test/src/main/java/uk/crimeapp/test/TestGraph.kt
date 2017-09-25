package uk.crimeapp.test

import uk.crimeapp.common.di.CrimeModule
import uk.crimeapp.common.di.Graph
import uk.crimeapp.common.di.LocationModule
import uk.crimeapp.common.di.NavigationModule

class TestGraph(
    private val original: Graph
) : Graph {
    var testNavigationModule: NavigationModule? = null

    var testLocationModule: LocationModule? = null
    override val navigationModule: NavigationModule
        get() = testNavigationModule ?: original.navigationModule

    override val locationModule: LocationModule
        get() = testLocationModule ?: original.locationModule

    override val crimeModule: CrimeModule
        get() = original.crimeModule

}
