package adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.smk.wherewasi.R
import io.realm.kotlin.ext.query
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import model.Location
import model.MyRealm


class CardLocationInfoAdapter(val listener: Listener) :
    RecyclerView.Adapter<CardLocationInfoAdapter.ViewHolder>() {

    private val realm = MyRealm.realm
    private val locationHistory = realm
        .query<Location>()
        .asFlow()
        .map { results ->
            results.list.toList()
        }
        .stateIn(
            MainScope(),
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

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
        return locationHistory.value.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            listener.onClick(position)
        }
        val cardView: CardView = holder.itemView as CardView
        cardView.findViewById<ImageView>(R.id.info_loc_image)
            .setImageResource(R.drawable.image_loc_place_holder)
        val latLng = "Lat: ${locationHistory.value[position].latitude}, Lon: ${locationHistory.value[position].latitude}"
        cardView.findViewById<TextView>(R.id.info_loc_text).text = latLng
        cardView.setOnClickListener {
            listener.onClick(position)
        }

    }
}