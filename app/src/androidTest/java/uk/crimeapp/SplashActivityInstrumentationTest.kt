package uk.crimeapp

import android.content.Intent
import com.pablisco.crimeapp.test.*
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import uk.crimeapp.common.di.NavigationModule
import uk.crimeapp.location.LocationActivity
import uk.crimeapp.test.TestApp
import uk.crimeapp.test.activityRule

class SplashActivityInstrumentationTest {

    private val intentCaptor = captor<Intent>()

    @get:Rule val rule = activityRule<SplashActivity> {
        TestApp.testApp().testGraph.testNavigationModule = mock<NavigationModule> {
            on(startActivity(notNull(), intentCaptor.safeCapture())).thenDoNothing()
        }
    }

    @Test fun shouldLaunchMainFeature() {
        assertThat(intentCaptor.allValues).apply {
            usingFieldByFieldElementComparator()
            containsOnly(Intent(rule.activity, LocationActivity::class.java))
        }
    }

}

