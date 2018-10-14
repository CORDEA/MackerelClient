package jp.cordea.mackerelclient.repository

import io.realm.Realm
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.model.UserMetric
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetricsLocalDataSource @Inject constructor() {
    fun getMetricsDefinition(hostId: String): List<UserMetric> =
        Realm.getDefaultInstance().use {
            it.copyFromRealm(
                it.where(UserMetric::class.java)
                    .equalTo("type", MetricsType.HOST.name)
                    .equalTo("parentId", hostId).findAll()
            )
        }

    fun storeDefaultUserMetrics(hostId: String) {
        val realm = Realm.getDefaultInstance()
        val c = realm.where(UserMetric::class.java)
            .equalTo("type", MetricsType.HOST.name)
            .equalTo("parentId", hostId).findAll().size
        if (c > 0) {
            realm.close()
            return
        }

        val maxId = (realm.where(UserMetric::class.java).max("id") ?: 0).toInt()
        val metrics = arrayListOf<UserMetric>().apply {
            add(UserMetric().apply {
                type = MetricsType.HOST.name
                id = maxId + 1
                this.parentId = parentId
                label = "loadavg5"
                metric0 = "loadavg5"
            })
            add(UserMetric().apply {
                type = MetricsType.HOST.name
                id = maxId + 2
                this.parentId = parentId
                label = "cpu percentage"
                metric0 = "cpu.system.percentage"
                metric1 = "cpu.user.percentage"
            })
            add(UserMetric().apply {
                type = MetricsType.HOST.name
                id = maxId + 3
                this.parentId = parentId
                label = "memory"
                metric0 = "memory.used"
                metric1 = "memory.free"
            })
        }

        realm.beginTransaction()
        realm.copyToRealm(metrics)
        realm.commitTransaction()
        realm.close()
    }
}
