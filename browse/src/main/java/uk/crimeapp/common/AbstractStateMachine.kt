package uk.crimeapp.common

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.experimental.launch

abstract class AbstractStateMachine<A, B> {

    val events = Channel<A>(UNLIMITED)

    val states = Channel<B>(UNLIMITED)

    init {
        launch(CommonPool) {
            while(true) {
                processEvent(events.receive())
            }
        }
    }

    abstract suspend fun processEvent(event: A)

    protected fun sendState(state: B) {
        states.offer(state)
    }

}