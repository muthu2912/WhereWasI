package com.smk.wherewasi.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class User : RealmObject {
    @PrimaryKey
    var userName: String = ""
    var password: String = ""

}