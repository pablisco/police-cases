package uk.crimeapp.location

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import uk.crimeapp.api.PoliceApiClient
import uk.crimeapp.common.di.DataModule
import uk.crimeapp.common.di.LocationModule
import uk.crimeapp.location.model.LocationEvent
import uk.crimeapp.location.model.LocationState

class InternalLocationModule(
    dataModule: DataModule
) : LocationModule {

    override val states: ReceiveChannel<LocationState>
        get() = stateMachine.states

    override val events: SendChannel<LocationEvent>
        get() = stateMachine.events

    private val api = PoliceApiClient(dataModule.httpClient)

    private val repository = LocationRepository(api)

    private val stateMachine = LocationStateMachine { repository.findAll() }

}