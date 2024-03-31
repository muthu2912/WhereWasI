package com.smk.wherewasi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.smk.wherewasi.R
import com.smk.wherewasi.model.Location


class CardLocationInfoAdapter(private val listener: Listener, private val locationHistory: List<Location>):
    RecyclerView.Adapter<CardLocationInfoAdapter.ViewHolder>() {
    interface Listener {
        fun onClick(position: Int)
    }

    class ViewHolder(cardView: CardView) : RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cv = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_location_info, parent, false) as CardView
        return ViewHolder(cv)
    }

    override fun getItemCount(): Int {
        return locationHistory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            listener.onClick(position)
        }
        val cardView: CardView = holder.itemView as CardView
        cardView.findViewById<ImageView>(R.id.info_loc_image)
            .setImageResource(R.drawable.image_loc_place_holder)
        cardView.setOnClickListener {
            listener.onClick(position)
        }
        val userText = "User: ${locationHistory[position].user}"
        val lat = "Latitude: ${locationHistory[position].latitude}"
        val lon = "Longiitude: ${locationHistory[position].longitude}"
        val time = "Timestamp: ${locationHistory[position].time}"

        cardView.findViewById<TextView>(R.id.info_loc_user_info).text = userText
        cardView.findViewById<TextView>(R.id.info_loc_lat).text = lat
        cardView.findViewById<TextView>(R.id.info_loc_lon).text = lon
        cardView.findViewById<TextView>(R.id.info_loc_time).text = time

    }
}