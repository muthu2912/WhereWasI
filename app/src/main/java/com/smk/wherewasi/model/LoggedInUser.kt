package com.smk.wherewasi.model

import io.realm.kotlin.types.RealmObject

class LoggedInUser : RealmObject {
    var user: String = ""
}