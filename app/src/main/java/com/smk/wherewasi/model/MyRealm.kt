package com.smk.wherewasi.model

import android.app.Application
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults

class MyRealm : Application() {
    companion object {
        lateinit var realm: Realm

        fun getLoggedInUser(): User? {
            val user: RealmResults<User> = realm.query<User>().find()
            return if (user.size == 0) null else user[0]
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