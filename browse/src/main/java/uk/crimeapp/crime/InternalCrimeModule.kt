package uk.crimeapp.crime

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import uk.crimeapp.api.PoliceApiClient
import uk.crimeapp.common.di.CrimeModule
import uk.crimeapp.common.di.DataModule
import uk.crimeapp.crime.model.CrimeEvent
import uk.crimeapp.crime.model.CrimeState

class InternalCrimeModule(
    dataModule: DataModule
) : CrimeModule {

    override val states: ReceiveChannel<CrimeState>
        get() = stateMachine.states
    override val events: SendChannel<CrimeEvent>
        get() = stateMachine.events

    private val api = PoliceApiClient(dataModule.httpClient)

    private val crimeRepository = CrimeRepository(api)

    private val stateMachine = CrimeStateMachine(
        crimesFromSlug = { crimeRepository.findAll(it) },
        crimesFromCoords = { crimeRepository.findAll(it) }
    )

}