package com.smk.wherewasi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smk.wherewasi.model.MyRealm

class MainViewModel: ViewModel() {
    //private val realm = MyRealm.realm
    val loggedInUser = MutableLiveData<String>()

    init {
        getUserProfile()
    }
    private fun getUserProfile(){
         loggedInUser.value= MyRealm.getLoggedInUser()?.userName
    }
}