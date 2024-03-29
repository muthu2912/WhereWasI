package com.smk.wherewasi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smk.wherewasi.MyRealm
import com.smk.wherewasi.model.User
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val realm = MyRealm.realm
    val loginResult = MutableLiveData<String>()
    fun onLoginClicked(username: String, password: String) {
        val users: RealmResults<User> = realm.query<User>(
            "userName=$0", username
        ).find()
        if(users.size==0){
            loginResult.value = "User does not exist"
        }else {
            val res = if (users[0].password == password) "Success" else "Failed"
            loginResult.value = res
        }
    }

    fun onSignupClicked(uname: String, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                val user = User().apply {
                    userName = uname
                    password = pass
                }
                copyToRealm(user, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }
}