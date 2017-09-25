package uk.crimeapp.test

import android.app.Application
import android.support.test.InstrumentationRegistry
import uk.crimeapp.common.di.Graph
import uk.crimeapp.common.di.HasGraph
import kotlin.properties.Delegates

class TestApp : Application(), HasGraph {

    companion object {
        fun testApp() : TestApp = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .applicationContext as TestApp
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(ProgressBarHunter())
    }

    var original: Application by Delegates.observable(this as Application) { _, _, app ->
        _testGraph = TestGraph(Graph.from(app))
    }

    private var _testGraph: TestGraph? = null

    val testGraph: TestGraph
        get() = _testGraph ?: throw IllegalStateException("Test graph not set.")

    override val graph: Graph
        get() = testGraph

}