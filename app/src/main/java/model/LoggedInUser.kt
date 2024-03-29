package model

import io.realm.kotlin.types.RealmObject

class LoggedInUser : RealmObject {
    var user: User? = null
}