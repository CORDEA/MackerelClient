package jp.cordea.mackerelclient.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class UserKey : RealmObject() {
    @PrimaryKey
    open var id: Int = 0

    open var name: String? = null
    open var email: String? = null

    @Required
    open var key: String? = null
}