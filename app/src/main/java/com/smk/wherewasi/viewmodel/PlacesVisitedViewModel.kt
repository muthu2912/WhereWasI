import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smk.wherewasi.model.Location
import com.smk.wherewasi.model.MyRealm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlacesVisitedViewModel : ViewModel() {
    private val realm = MyRealm.realm
    private val _locationHistory = MutableLiveData<List<Location>>()
    val locationHistory: LiveData<List<Location>> = _locationHistory

    private val locationData = realm
        .query<Location>("user=$0", "muthu")
        .asFlow()
        .map { results -> results.list.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            locationData.collect { locations ->
                // Use postValue to update LiveData on the main thread
                _locationHistory.postValue(locations)
            }
        }
    }
}
