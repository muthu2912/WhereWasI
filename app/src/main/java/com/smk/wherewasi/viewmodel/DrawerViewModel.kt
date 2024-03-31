package com.smk.wherewasi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.mikepenz.materialdrawer.model.interfaces.descriptionText
import com.mikepenz.materialdrawer.model.interfaces.iconRes
import com.mikepenz.materialdrawer.model.interfaces.nameText
import com.smk.wherewasi.R
import com.smk.wherewasi.model.CurrentUser
import com.smk.wherewasi.model.MyRealm
import com.smk.wherewasi.model.User
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrawerViewModel : ViewModel() {
    private val realm = MyRealm.realm
    private lateinit var registeredUsers: List<User>
    lateinit var iProfiles: List<IProfile>
    var currentUserIndex: Long = 0

    init{
        getRegisteredUsers()
        setProfiles()
    }
    private fun setProfiles() {
        val profiles = mutableListOf<IProfile>()
        val loggedInUser = MyRealm.getCurrentUser()
        for (i in registeredUsers.indices) {
            val uname = registeredUsers[i].userName
            if (loggedInUser.equals(uname)) currentUserIndex = i.toLong()
            val profile = ProfileDrawerItem().apply {
                nameText = uname; descriptionText =
                "$uname@gmail.com"; iconRes = R.drawable.profile; identifier = i.toLong()
            }
            profiles.add(profile)
        }
        iProfiles = profiles
    }

    private fun getRegisteredUsers() {
        registeredUsers =  realm.query<User>().find()
    }

    fun setCurrentUser(currentUserIdentifier: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            MyRealm.realm.write {
                val currentUser = CurrentUser().apply {
                    user = registeredUsers[currentUserIdentifier].userName
                }
                copyToRealm(currentUser, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

}