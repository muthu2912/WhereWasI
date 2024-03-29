package viewmodel

import androidx.lifecycle.ViewModel
import model.MyRealm

class PlacesVisitedViewModel: ViewModel(){
    private val realm = MyRealm.realm
     val loggedInUser = ""
}