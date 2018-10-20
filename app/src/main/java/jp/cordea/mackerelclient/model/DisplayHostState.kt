package jp.cordea.mackerelclient.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class DisplayHostState(
    @PrimaryKey var name: String = "",
    var isDisplay: Boolean = false
) : RealmObject()
