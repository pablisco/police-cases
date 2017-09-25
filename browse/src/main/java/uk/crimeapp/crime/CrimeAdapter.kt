package uk.crimeapp.crime

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_crime.view.*
import uk.crimeapp.common.android.startActivity
import uk.crimeapp.crime.model.Crime
import uk.crimeapp.crime.model.CrimeLocation
import uk.crimeapp.main.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CrimeAdapter(
    private val items: List<Crime>
) : RecyclerView.Adapter<CrimeAdapter.CrimeViewHolder>() {

    companion object {
        val DATE_FORMAT: DateFormat = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder =
        CrimeViewHolder(parent)

    override fun getItemCount(): Int =
        items.size

    override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class CrimeViewHolder(
        parent: ViewGroup
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_crime, parent, false)
    ) {

        private val categoryView = itemView.categoryView
        private val outcomeView = itemView.outcomeView
        private val locationView = itemView.locationView
        private val dateView = itemView.dateView
        private val mapButton = itemView.mapButton
        private val expander = itemView.expander
        private val collapseViewGroup = itemView.collapseViewGroup

        init {
            itemView.setOnClickListener { toggle() }
        }

        fun bind(crime: Crime) {
            categoryView.text = crime.category
            outcomeView.text = crime.outcomeStatus
            dateView.text = DATE_FORMAT.format(crime.date)
            val location = crime.location
            if(location != null) {
                bindLocation(location, crime)
            } else {
                hideLocation()
            }
            isCollapsed = true
        }

        private fun bindLocation(location: CrimeLocation, crime: Crime) {
            locationView.text = location.street.name
            mapButton.isEnabled = true
            mapButton.setOnClickListener {
                itemView.context.startActivity {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse("http://maps.google.com/maps?q=loc:${location.latitude},${location.longitude}(${crime.category})")
                }
            }
        }

        private fun hideLocation() {
            locationView.text = "Unknown"
            mapButton.isEnabled = false
        }

        private var isCollapsed: Boolean
            get() = collapseViewGroup.visibility == View.GONE
            set(collapse) = if(collapse) collapse() else expand()

        private fun collapse() {
            expander.setImageResource(R.drawable.ic_expand_more)
            collapseViewGroup.visibility = View.GONE
        }

        private fun expand() {
            expander.setImageResource(R.drawable.ic_expand_less)
            collapseViewGroup.visibility = View.VISIBLE
        }

        private fun toggle() {
            isCollapsed = !isCollapsed
        }

    }

}