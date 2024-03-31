package com.smk.wherewasi.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.smk.wherewasi.model.MyRealm.Companion.realm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyRealm : Application() {
    companion object {
        lateinit var realm: Realm

        fun getLoggedInUser(): String? {
            val currentUser = realm.query<LoggedInUser>().find()
            return if(currentUser.size!=0) currentUser[0].user else null
        }

        fun deleteLoggedInUser() {
            realm.writeBlocking {
                val loggedInUser = query<LoggedInUser>().find()
                delete(loggedInUser.first())
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
        realm = Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    LoggedInUser::class,
                    Location::class,
                    User::class
                )
            )
        )
    }
}