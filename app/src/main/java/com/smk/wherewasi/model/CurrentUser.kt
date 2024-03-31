package com.smk.wherewasi.model

import io.realm.kotlin.types.RealmObject

class CurrentUser : RealmObject {
    var user: String = ""
}