package viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import model.LoggedInUser
import model.MyRealm
import model.User
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
        if (users.size == 0) {
            loginResult.value = "User does not exist"
        } else {
            if (users[0].password == password) {
                setLoggedInUser(users[0]) //TODO clear the table and set logged in user
                loginResult.value = "Success"
            } else loginResult.value = "Failed"

        }
    }

    fun onSignupClicked(uname: String, pass: String) {
        if (getUserCount() > 3) {
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

    private fun setLoggedInUser(currentUser: User) {
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                val loggedInUser = LoggedInUser().apply {
                    user = currentUser
                }
                copyToRealm(loggedInUser, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    private fun getUserCount(): Int {
        return realm.query<User>().find().size
    }
}