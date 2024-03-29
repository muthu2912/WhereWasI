package model

import io.realm.kotlin.types.EmbeddedRealmObject

class Location : EmbeddedRealmObject {
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var time: String = ""
    var user: User? = null
}