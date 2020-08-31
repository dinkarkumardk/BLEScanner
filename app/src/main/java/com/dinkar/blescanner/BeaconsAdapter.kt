package com.dinkar.blescanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


class BeaconsAdapter(beacons: List<Beacon>) :
    RecyclerView.Adapter<BeaconsAdapter.BeaconHolder>(), Filterable {
    var beaconList: MutableList<Beacon> = beacons.toMutableList()
    var beaconListFiltered: MutableList<Beacon> = beacons.toMutableList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeaconHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BeaconHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: BeaconHolder, position: Int) {
        val beacon: Beacon = beaconListFiltered[position]
        holder.bind(beacon)
    }

    override fun getItemCount() = beaconListFiltered.size

    fun updateData(data: List<Beacon>, beaconTypePositionSelected: Int) {
        beaconList.clear()
        beaconList.addAll(data)
        setBeaconFilter(beaconTypePositionSelected)
        notifyDataSetChanged()
    }

    private fun setBeaconFilter(position: Int) {
        when (position) {
            0 -> {
                filter.filter(Utils.ALL)
            }
            1 -> {
                filter.filter(Utils.EDDYSTONE)
            }
            2 -> {
                filter.filter(Utils.IBEACON)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    beaconListFiltered = beaconList
                } else {
                    val filteredList: MutableList<Beacon> = ArrayList()
                    for (beacon in beaconList) {
                        if (beacon.type == Utils.getBeaconFilterFromString(charString) || Utils.getBeaconFilterFromString(
                                charString
                            ) == Beacon.beaconType.any
                        ) {
                            filteredList.add(beacon)
                        }
                    }
                    beaconListFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = beaconListFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                beaconListFiltered = filterResults.values as ArrayList<Beacon>
                notifyDataSetChanged()
            }
        }
    }

    class BeaconHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.scan_result_items, parent, false)) {
        private var image: ImageView? = null
        private var mac: TextView? = null
        private var namespaceUUID: TextView? = null
        private var instanceMajorMinor: TextView? = null
        private var rssi: TextView? = null
        private val context = parent.context

        init {
            image = itemView.findViewById(R.id.beacon_image)
            mac = itemView.findViewById(R.id.beacon_mac)
            namespaceUUID = itemView.findViewById(R.id.beacon_namespcae_uuid)
            instanceMajorMinor = itemView.findViewById(R.id.beacon_instance_major_minor)
            rssi = itemView.findViewById(R.id.beacon_rssi)
        }

        fun bind(beacon: Beacon) {
            mac?.text = String.format(
                context.getString(R.string.mac),
                beacon.macAddress
            )
            if (beacon.type == Beacon.beaconType.iBeacon) {
                namespaceUUID?.text = String.format(context.getString(R.string.uuid), beacon.uuid)
                instanceMajorMinor?.text = String.format(
                    context.getString(R.string.major_minor),
                    beacon.major,
                    beacon.minor
                )
                image?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ibeacon))
                instanceMajorMinor?.visibility = View.VISIBLE
                namespaceUUID?.visibility = View.VISIBLE
            } else if (beacon.type == Beacon.beaconType.eddystoneUID) {
                namespaceUUID?.text =
                    String.format(context.getString(R.string.namespace), beacon.namespace)
                instanceMajorMinor?.text = String.format(
                    context.getString(R.string.instance),
                    beacon.instance
                )
                image?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eddystone))
                instanceMajorMinor?.visibility = View.VISIBLE
                namespaceUUID?.visibility = View.VISIBLE
            } else {
                if (!beacon.manufacturer.isNullOrBlank()) {
                    namespaceUUID?.text = beacon.manufacturer
                    namespaceUUID?.visibility = View.VISIBLE
                } else {
                    namespaceUUID?.visibility = View.GONE
                }
                instanceMajorMinor?.visibility = View.GONE
                image?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bluetooth))
            }


            rssi?.text = String.format(
                context.getString(R.string.rssi),
                beacon.rssi
            )

        }

    }
}


