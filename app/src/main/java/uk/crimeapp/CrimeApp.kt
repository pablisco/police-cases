package uk.crimeapp

import android.app.Application
import uk.crimeapp.common.di.Graph
import uk.crimeapp.common.di.HasGraph

open class CrimeApp(
    override val graph: Graph = AppGraph()
) : Application(), HasGraph