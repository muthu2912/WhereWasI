package view.fragment

import adapter.CardLocationInfoAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smk.wherewasi.R

class PlacesVisitedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val locHistoryRecycler =
            inflater.inflate(R.layout.fragment_places_visited,container,false) as RecyclerView
        locHistoryRecycler.layoutManager = GridLayoutManager(activity,1)
        val listener = object  : CardLocationInfoAdapter.Listener{
            override fun onClick(position: Int) {
                Toast.makeText(context, "Item clicked at position: $position", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        locHistoryRecycler.adapter = CardLocationInfoAdapter(listener)
        return locHistoryRecycler
    }

}