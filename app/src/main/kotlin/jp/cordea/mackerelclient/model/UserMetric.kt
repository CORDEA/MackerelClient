package jp.cordea.mackerelclient.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by Yoshihiro Tanaka on 16/01/20.
 */
public open class UserMetric : RealmObject() {
    @PrimaryKey
    public open var id: Int = 0

    @Required
    public open var parentId: String? = null

    @Required
    public open var type: String? = null
    public open var label: String? = null

    @Required
    public open var metric0: String? = null
    public open var metric1: String? = null
}