package uk.crimeapp.common.di

import android.content.Context
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface Graph {

    companion object {
        fun from(context: Context?): Graph = when {
            context == null -> throw IllegalArgumentException("Context cannot be null")
            context is HasGraph -> context.graph
            context.applicationContext != null && context.applicationContext != context -> from(context.applicationContext)
            else -> throw IllegalArgumentException("Context '$context' must extend HasGraph")
        }
    }

    val navigationModule: NavigationModule

    val locationModule: LocationModule

    val crimeModule: CrimeModule

}

/**
 * Adds our graph as a property to all the context objects
 */
val Context.graph: Graph
    get() = Graph.from(this)

/**
 * Used along with [GraphProperty] to aid injection of objects in the graph
 */
fun <T> Context.graph(f: Graph.() -> T) = GraphProperty(f) { graph }

/**
 * Delegate property with a backing graph factory to resolve the object required
 */
class GraphProperty<out T>(
    private val f: Graph.() -> T,
    private val graphFactory: () -> Graph
) : ReadOnlyProperty<Context, T> {

    private val value by lazy { f(graphFactory()) }

    override fun getValue(thisRef: Context, property: KProperty<*>): T = value

}

/**
 * Describes a type as a provider of graph. i.e.: the Application object
 */
interface HasGraph {
    val graph: Graph
}