package jp.cordea.mackerelclient.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class DisplayHostState : RealmObject() {
    @PrimaryKey
    open var name: String = ""

    @Required
    open var isDisplay: Boolean? = null
}
