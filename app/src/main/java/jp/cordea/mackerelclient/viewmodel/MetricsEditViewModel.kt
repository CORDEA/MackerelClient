package jp.cordea.mackerelclient.viewmodel

import io.realm.Realm
import jp.cordea.mackerelclient.model.UserMetric

class MetricsEditViewModel {

    fun getMetric(id: Int): UserMetric {
        val realm = Realm.getDefaultInstance()
        val metric = realm.copyFromRealm(
            realm.where(UserMetric::class.java).equalTo("id", id).findFirst()!!
        )
        realm.close()
        return metric
    }

    fun storeMetric(
        id: Int,
        parentId: String,
        type: String,
        label: String,
        metric0: String,
        metric1: String?
    ) {
        val realm = Realm.getDefaultInstance()
        val maxId = realm.where(UserMetric::class.java).max("id")
        val item = UserMetric()
        item.id = if (id != -1) id else if (maxId == null) 0 else (maxId.toInt() + 1)
        item.parentId = parentId
        item.type = type
        item.label = label
        item.metric0 = metric0
        if (!metric1.isNullOrBlank()) {
            item.metric1 = metric1
        }

        realm.executeTransaction {
            realm.copyToRealmOrUpdate(item)
        }
    }
}
