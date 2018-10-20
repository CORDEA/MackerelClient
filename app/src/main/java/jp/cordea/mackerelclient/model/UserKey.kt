package jp.cordea.mackerelclient.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserKey(
    @PrimaryKey var id: Int = 0,
    var name: String? = null,
    var email: String? = null,
    var key: String = ""
) : RealmObject()
