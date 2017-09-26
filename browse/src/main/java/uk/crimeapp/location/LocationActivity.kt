package uk.crimeapp.location

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.item_location.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import uk.crimeapp.common.android.start
import uk.crimeapp.common.android.visibleWhen
import uk.crimeapp.common.di.graph
import uk.crimeapp.crime.CrimeActivity
import uk.crimeapp.crime.locationName
import uk.crimeapp.location.model.Location
import uk.crimeapp.location.model.Location.Companion.NO_LOCATION
import uk.crimeapp.location.model.LocationEvent
import uk.crimeapp.location.model.LocationState
import uk.crimeapp.main.R
import kotlin.properties.Delegates.observable

class LocationActivity : AppCompatActivity() {

    private val events by graph { locationModule.events }
    private val states by graph { locationModule.states }

    private val stateViews by lazy { listOf(loading, list, error) }

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        list.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        job = launch(CommonPool) {
            while (isActive) {
                waitForNextState()
            }
        }
        events.offer(LocationEvent.Load)
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }

    private suspend fun waitForNextState() {
        val state = states.receive()
        launch(UI) {
            when (state) {
                is LocationState.Loading -> onLoading()
                is LocationState.Loaded -> onLoaded(state.locations)
                is LocationState.Failure -> onError()
                is LocationState.Navigate -> onNavigate(state.location)
            }
        }
    }

    private fun onLoading() {
        stateViews.visibleWhen { it == loading }
    }

    private fun onLoaded(locations: List<Location>) {
        stateViews.visibleWhen { it == list }
        list.adapter = LocationAdapter(locations) { events.offer(LocationEvent.Select(it)) }
    }

    private fun onError() {
        stateViews.visibleWhen { it == error }
    }

    private fun onNavigate(location: Location) {
        start<CrimeActivity> {
            data = Uri.parse("crime://${location.slug}")
            locationName = location.name
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun retry(ignored: View) {
        events.offer(LocationEvent.Reload)
    }

    class LocationAdapter(
        private val items: List<Location>,
        private val onSelect: (Location) -> Unit
    ) : RecyclerView.Adapter<LocationViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder =
            LocationViewHolder(parent, onSelect)

        override fun getItemCount(): Int =
            items.size

        override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
            holder.data = items[position]
        }
    }

    class LocationViewHolder(
        parent: ViewGroup,
        private val onSelect: (Location) -> Unit
    ) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)) {

        private val locationNameView = itemView.locationNameView
        private val locationIcon = itemView.locationIcon

        var data: Location by observable(NO_LOCATION) { _, _, l -> bind(l) }

        private fun bind(location: Location) {
            locationIcon.visibleWhen(invisibleAs = INVISIBLE) {
                location == Location.NEAR_LOCATION
            }
            locationNameView.text = location.name
            itemView.setOnClickListener {
                onSelect(location)
            }
        }

    }

}
