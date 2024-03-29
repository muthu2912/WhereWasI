package com.smk.wherewasi.model

import io.realm.kotlin.types.RealmObject

class Location : RealmObject {
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var time: String = ""
    var user: String = "" //Todo: implement user id to reduce storage
}