package com.smk.wherewasi.model

import android.app.Application
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query

class MyRealm : Application() {
    companion object {
        lateinit var realm: Realm

        fun getCurrentUser(): String? {
            val currentUser = realm.query<CurrentUser>().find()
            return if(currentUser.size!=0) currentUser[0].user else null
        }

        fun removeCurrentUser() {
            realm.writeBlocking {
                val currentUser = query<CurrentUser>().find()
                delete(currentUser.first())
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
        realm = Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    CurrentUser::class,
                    Location::class,
                    User::class
                )
            )
        )
    }
}