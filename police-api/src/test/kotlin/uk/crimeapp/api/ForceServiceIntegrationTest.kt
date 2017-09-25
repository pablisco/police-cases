package uk.crimeapp.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ForceServiceIntegrationTest {

    companion object {
        private val LONDON_FORCE = Force("city-of-london", "City of London Police")
    }

    @Test fun `should provide all forces`() {
        val service = PoliceApiClient().forceService

        val results = service.findAll().execute().body()
        assertThat(results).contains(LONDON_FORCE)
    }

}