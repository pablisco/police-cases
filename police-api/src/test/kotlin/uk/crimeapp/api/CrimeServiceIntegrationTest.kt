package uk.crimeapp.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CrimeServiceIntegrationTest {

    @Test
    fun `should provide all forces`() {
        val service = PoliceApiClient().crimeService

        val results = service.findAllBy("city-of-london").execute().body()

        assertThat(results).isNotEmpty
    }

}