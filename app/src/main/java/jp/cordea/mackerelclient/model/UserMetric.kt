package jp.cordea.mackerelclient.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserMetric(
    @PrimaryKey var id: Int = 0,
    var parentId: String = "",
    var type: String = "",
    var label: String? = null,
    var metric0: String = "",
    var metric1: String? = null
) : RealmObject()
