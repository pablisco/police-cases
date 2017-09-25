package uk.crimeapp.common.di

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import uk.crimeapp.crime.model.CrimeEvent
import uk.crimeapp.crime.model.CrimeState

interface CrimeModule {

    val states: ReceiveChannel<CrimeState>

    val events: SendChannel<CrimeEvent>

}