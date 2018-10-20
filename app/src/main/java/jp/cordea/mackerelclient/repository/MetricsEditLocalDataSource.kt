package jp.cordea.mackerelclient.repository

import io.realm.Realm
import io.realm.kotlin.createObject
import jp.cordea.mackerelclient.model.UserMetric
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetricsEditLocalDataSource @Inject constructor() {
    fun getMetric(id: Int): UserMetric =
        Realm.getDefaultInstance().use {
            it.copyFromRealm(
                it.where(UserMetric::class.java).equalTo("id", id).findFirst()!!
            )
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
        val query = realm.where(UserMetric::class.java)
        if (id >= 0) {
            val metric = query.equalTo("id", id).findFirst()!!
            realm.executeTransaction {
                metric.parentId = parentId
                metric.type = type
                metric.label = label
                metric.metric0 = metric0
                if (!metric1.isNullOrBlank()) {
                    metric.metric1 = metric1
                }
            }
            realm.close()
            return
        }

        val maxId = query.max("id")
        val nextId = if (maxId == null) 0 else maxId.toInt() + 1
        realm.executeTransaction {
            realm.createObject<UserMetric>(nextId).apply {
                this.parentId = parentId
                this.type = type
                this.label = label
                this.metric0 = metric0
                if (!metric1.isNullOrBlank()) {
                    this.metric1 = metric1
                }
            }
        }
        realm.close()
    }
}
