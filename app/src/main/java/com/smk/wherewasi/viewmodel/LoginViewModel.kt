package com.smk.wherewasi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smk.wherewasi.model.CurrentUser
import com.smk.wherewasi.model.MyRealm
import com.smk.wherewasi.model.User
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val realm = MyRealm.realm
    val loginResult = MutableLiveData<String>()
    val signupResult = MutableLiveData<String>()
    fun onLoginClicked(username: String, password: String) {
        val users: RealmResults<User> = realm.query<User>(
            "userName=$0", username
        ).find()
        if (users.isEmpty()) {
            loginResult.value = "User does not exist"
        } else {
            if (users[0].password == password) {
                MyRealm.removeCurrentUser()
                setCurrentUser(users[0].userName)
                loginResult.value = "Success"
            } else loginResult.value = "Failed"

        }
    }

    fun onSignupClicked(uname: String, pass: String) {
        if (getUserCount() >= 5) {
            signupResult.value = "User limit exceeds"
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                realm.write {
                    val user = User().apply {
                        userName = uname
                        password = pass
                    }
                    copyToRealm(user, updatePolicy = UpdatePolicy.ALL)
                }
            }
            signupResult.value = "Signup Successfully"
        }
    }

    private fun setCurrentUser(currentUserParams: String) {
        viewModelScope.launch(Dispatchers.IO) {
            MyRealm.realm.write {
                val currentUser = CurrentUser().apply {
                    user = currentUserParams
                }
                copyToRealm(currentUser, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    private fun getUserCount(): Int {
        return realm.query<User>().find().size
    }
}