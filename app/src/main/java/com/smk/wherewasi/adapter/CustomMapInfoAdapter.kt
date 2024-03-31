package com.smk.wherewasi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.smk.wherewasi.R

class CustomMapInfoAdapter(context: Context) : GoogleMap.InfoWindowAdapter {

    private val inflater = LayoutInflater.from(context)

    @SuppressLint("MissingInflatedId", "InflateParams")
    override fun getInfoWindow(marker: Marker): View? {
        val view = inflater.inflate(R.layout.custom_map_info_window, null)
        val titleTextView = view.findViewById<TextView>(R.id.title_text)
        val snippetTextView = view.findViewById<TextView>(R.id.snippet_text)

        val title = marker.title
        val snippet = marker.snippet
        titleTextView.text = title
        snippetTextView.text = snippet
        return view
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }
}
