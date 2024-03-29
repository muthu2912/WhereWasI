package com.smk.wherewasi

import android.app.Application
import com.smk.wherewasi.model.*
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MyRealm : Application() {
    companion object {
        lateinit var realm: Realm
    }

    override fun onCreate() {
        super.onCreate()
        realm = Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    Location::class,
                    User::class
                )
            )
        )
    }
}