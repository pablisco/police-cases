package uk.crimeapp.main.location

import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withTimeout
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import uk.crimeapp.location.LocationStateMachine
import uk.crimeapp.location.model.Location
import uk.crimeapp.location.model.LocationEvent
import uk.crimeapp.location.model.LocationState
import uk.crimeapp.location.model.LocationState.*
import java.util.concurrent.TimeUnit

class LocationStateMachineTest {

    companion object {
        private val LONDON = Location("London", "london")
        private val ESSEX = Location("Essex", "essex")
    }

    private var dataSource: suspend () -> List<Location> = {
        listOf(LONDON)
    }

    private val stateMachine by lazy { LocationStateMachine(dataSource) }

    @Test fun shouldRetrieveData() {
        val states = runWithTimeout {
            stateMachine.events.send(LocationEvent.Load)
            receiveStates(2)
        }

        assertThat(states).containsOnly(
            Loading,
            Loaded(listOf(LONDON))
        )
    }

    @Test fun shouldReportError() {
        val states = runWithTimeout {
            dataSource = { throw RuntimeException() }
            sendEvents(LocationEvent.Load)
            receiveStates(2)
        }

        assertThat(states).containsOnly(Loading, Failure)
    }

    @Test fun shouldSaveInMemoryCache() {
        val states = runWithTimeout {
            dataSource = limitedDataSource({ listOf(LONDON) }, { listOf(ESSEX) })
            sendEvents(LocationEvent.Load, LocationEvent.Load)
            receiveStates(3)
        }

        assertThat(states).containsOnly(
            Loading,
            Loaded(listOf(LONDON)),
            Loaded(listOf(LONDON))
        )
    }

    @Test fun shouldReload() {
        val states = runWithTimeout {
            dataSource = limitedDataSource({ listOf(LONDON) }, { listOf(ESSEX) })
            sendEvents(LocationEvent.Load, LocationEvent.Reload)
            receiveStates(4)
        }

        assertThat(states).containsOnly(
            Loading, Loaded(listOf(LONDON)),
            Loading, Loaded(listOf(ESSEX))
        )
    }

    @Test fun shouldRecoverFromError() {
        val states = runWithTimeout {
            dataSource = limitedDataSource({ throw RuntimeException() }, { listOf(LONDON) })
            sendEvents(LocationEvent.Load, LocationEvent.Reload)
            receiveStates(4)
        }

        assertThat(states).containsOnly(
            Loading, Failure,
            Loading, Loaded(listOf(LONDON))
        )

    }

    private suspend fun sendEvents(vararg events: LocationEvent) =
        events.forEach { stateMachine.events.send(it) }

    private suspend fun receiveStates(count: Int): List<LocationState> =
        (1..count).map { stateMachine.states.receive() }

    private fun limitedDataSource(vararg lists: () -> List<Location>): suspend () -> List<Location> {
        val items = mutableListOf(*lists)
        return {
            when {
                items.isEmpty() -> throw RuntimeException()
                else -> items.removeAt(0)()
            }
        }
    }

    private fun <T> runWithTimeout(time: Long = 5000L, unit: TimeUnit = TimeUnit.MILLISECONDS, block: suspend () -> T) =
        runBlocking { withTimeout(time, unit, block) }

}