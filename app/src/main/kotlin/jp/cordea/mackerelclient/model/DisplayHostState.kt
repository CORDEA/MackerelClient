package jp.cordea.mackerelclient.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by CORDEA on 2016/01/20.
 */
open class DisplayHostState : RealmObject() {
    @PrimaryKey
    open var name: String = ""

    @Required
    open var isDisplay: Boolean? = null
}
