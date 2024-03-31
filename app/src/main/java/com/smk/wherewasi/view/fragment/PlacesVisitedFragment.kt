package com.smk.wherewasi.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smk.wherewasi.R
import com.smk.wherewasi.adapter.CardLocationInfoAdapter
import com.smk.wherewasi.viewmodel.PlacesVisitedViewModel


class PlacesVisitedFragment : Fragment() {

    private lateinit var viewModel: PlacesVisitedViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[PlacesVisitedViewModel::class.java]

        val locHistoryRecycler =
            inflater.inflate(R.layout.fragment_places_visited,container,false) as RecyclerView
        locHistoryRecycler.layoutManager = GridLayoutManager(activity,1)

        val listener = object  : CardLocationInfoAdapter.Listener{
            override fun onClick(position: Int) {

                val mapFragment = MapFragment.newInstance(viewModel.locationHistory.value!![position])
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.frame_container, mapFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

        }

        viewModel.locationHistory.observe(viewLifecycleOwner){locations ->
            locHistoryRecycler.adapter = CardLocationInfoAdapter(listener,locations)
        }

        return locHistoryRecycler
    }

}