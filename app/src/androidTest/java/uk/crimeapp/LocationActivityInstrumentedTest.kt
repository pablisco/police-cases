package uk.crimeapp

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnHolderItem
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToHolder
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.Visibility.GONE
import com.pablisco.crimeapp.test.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import uk.crimeapp.common.di.LocationModule
import uk.crimeapp.common.di.NavigationModule
import uk.crimeapp.crime.CrimeActivity
import uk.crimeapp.location.LocationActivity
import uk.crimeapp.location.LocationActivity.LocationViewHolder
import uk.crimeapp.location.model.Location
import uk.crimeapp.location.model.LocationEvent
import uk.crimeapp.location.model.LocationState
import uk.crimeapp.main.R
import uk.crimeapp.test.TestApp
import uk.crimeapp.test.activityRule
import uk.crimeapp.test.match


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LocationActivityInstrumentedTest {

    companion object {
        val LONDON = Location("London", "london")
        val ESSEX = Location("Essex", "essex")
    }

    private val locationModule = object : LocationModule {
        override val states = Channel<LocationState>()
        override val events = Channel<LocationEvent>()
    }

    @get:Rule val rule = activityRule<LocationActivity> {
        with(TestApp.testApp().testGraph) {
            testLocationModule = locationModule
        }
    }

    @Test fun shouldShowLoadingState() {
        launch(CommonPool) {
            locationModule.states.send(LocationState.Loading)
        }

        onView(withId(R.id.loading))
            .check(matches(isDisplayed()))
        onView(withId(R.id.error))
            .check(matches(withEffectiveVisibility(GONE)))
        onView(withId(R.id.list))
            .check(matches(withEffectiveVisibility(GONE)))
    }

    @Test
    fun shouldShowLoadedState() {
        launch(CommonPool) {
            locationModule.states.send(LocationState.Loaded(listOf(LONDON, ESSEX)))
        }

        onView(withId(R.id.loading))
            .check(matches(withEffectiveVisibility(GONE)))
        onView(withId(R.id.error))
            .check(matches(withEffectiveVisibility(GONE)))

        onView(withId(R.id.list))
            .check(matches(isDisplayed()))
            .perform(scrollToHolder<LocationViewHolder>(match { it.data == LONDON }))
            .check(matches(hasDescendant(withText(LONDON.name))))
            .perform(scrollToHolder<LocationViewHolder>(match { it.data == ESSEX }))
            .check(matches(hasDescendant(withText(ESSEX.name))))
            .perform(actionOnHolderItem<LocationViewHolder>(match { it.data == ESSEX }, click()))

    }

    @Test
    fun shouldForwardToCrimeActivity() {
        val intentCaptor = captor<Intent>()
        TestApp.testApp().testGraph.testNavigationModule = mock<NavigationModule> {
            on(startActivity(notNull(), intentCaptor.safeCapture())).thenDoNothing()
        }

        launch(CommonPool) {
            locationModule.states.send(LocationState.Loaded(listOf(LONDON, ESSEX)))
        }

        assertThat(intentCaptor.allValues).apply {
            usingRecursiveFieldByFieldElementComparator()
            containsOnly(Intent(rule.activity, CrimeActivity::class.java))
        }
    }

    @Test
    fun shouldShowErrorState() {
        val events = mutableListOf<LocationEvent>()
        launch(CommonPool) {
            locationModule.states.send(LocationState.Failure)
            while (isActive) {
                events += locationModule.events.receive()
            }
        }

        onView(withId(R.id.loading))
            .check(matches(withEffectiveVisibility(GONE)))
        onView(withId(R.id.error))
            .check(matches(isDisplayed()))
        onView(withId(R.id.list))
            .check(matches(withEffectiveVisibility(GONE)))

        onView(withId(R.id.retryButton))
            .perform(click())

        assertThat(events).containsOnly(LocationEvent.Reload)
    }

}
