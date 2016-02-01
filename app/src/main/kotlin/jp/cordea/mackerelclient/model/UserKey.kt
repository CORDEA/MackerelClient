package jp.cordea.mackerelclient.model

/**
 * Created by Yoshihiro Tanaka on 16/01/22.
 */

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

public open class UserKey : RealmObject() {
    @PrimaryKey
    public open var id: Int = 0

    public open var name: String? = null
    public open var email: String? = null

    @Required
    public open var key: String? = null
}
