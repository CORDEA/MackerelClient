package jp.cordea.mackerelclient.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by CORDEA on 2016/01/20.
 */
public open class DisplayHostState : RealmObject() {
    @PrimaryKey
    public open var name: String = ""

    @Required
    public open var isDisplay: Boolean? = null
}