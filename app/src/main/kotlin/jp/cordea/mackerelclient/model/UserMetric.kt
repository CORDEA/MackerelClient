package jp.cordea.mackerelclient.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by Yoshihiro Tanaka on 16/01/20.
 */
open class UserMetric : RealmObject() {
    @PrimaryKey
    open var id: Int = 0

    @Required
    open var parentId: String? = null

    @Required
    open var type: String? = null
    open var label: String? = null

    @Required
    open var metric0: String? = null
    open var metric1: String? = null
}
