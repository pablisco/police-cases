package uk.crimeapp.common.di

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import uk.crimeapp.location.model.LocationEvent
import uk.crimeapp.location.model.LocationState

interface LocationModule {

    val states: ReceiveChannel<LocationState>

    val events: SendChannel<LocationEvent>

}