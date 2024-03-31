package com.smk.wherewasi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.mikepenz.materialdrawer.model.interfaces.descriptionText
import com.mikepenz.materialdrawer.model.interfaces.iconRes
import com.mikepenz.materialdrawer.model.interfaces.nameText
import com.smk.wherewasi.R
import com.smk.wherewasi.model.LoggedInUser
import com.smk.wherewasi.model.MyRealm
import com.smk.wherewasi.model.User
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrawerViewModel : ViewModel() {
    private val realm = MyRealm.realm
    private var registeredUsers: List<User>

    init{
        registeredUsers = getRegisteredUsers()
    }
    fun getProfiles(): List<IProfile> {
        val profiles = mutableListOf<IProfile>()
        for (i in registeredUsers.indices) {
            val profile = ProfileDrawerItem().apply {
                val uname = registeredUsers[i].userName
                nameText = uname; descriptionText =
                "$uname@gmail.com"; iconRes = R.drawable.profile; identifier = i.toLong();
            }
            profiles.add(profile)
        }
        return profiles
    }

    private fun getRegisteredUsers(): List<User> {
        return realm.query<User>().find()
    }

    fun setLoggedInUser(currentUserIdentifier: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            MyRealm.realm.write {
                val loggedInUser = LoggedInUser().apply {
                    user = registeredUsers[currentUserIdentifier].userName
                }
                copyToRealm(loggedInUser, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

}