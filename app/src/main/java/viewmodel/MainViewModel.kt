package viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.MyRealm

class MainViewModel: ViewModel() {
    private val real = MyRealm.realm
    val loggedInUser = MutableLiveData<String>()

    fun getUserProfile(){
         loggedInUser.value=MyRealm.getLoggedInUser()?.userName
    }
}