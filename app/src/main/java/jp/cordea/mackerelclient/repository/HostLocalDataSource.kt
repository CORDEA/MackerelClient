package jp.cordea.mackerelclient.repository

import io.realm.Realm
import io.realm.kotlin.createObject
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.model.DisplayHostState
import jp.cordea.mackerelclient.model.UserMetric
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HostLocalDataSource @Inject constructor() {
    fun getDisplayHostStates(defaults: Array<String>): List<DisplayHostState> {
        val realm = Realm.getDefaultInstance()
        if (realm.where(DisplayHostState::class.java).count() == 0L) {
            realm.executeTransaction {
                for (key in defaults) {
                    it.createObject<DisplayHostState>(key).apply {
                        isDisplay = (key == "standby" || key == "working")
                    }
                }
            }
        }
        return realm.copyFromRealm(realm.where(DisplayHostState::class.java).findAll())
    }

    fun deleteOldMetrics(hosts: List<String>) {
        val realm = Realm.getDefaultInstance()
        val results = realm.where(UserMetric::class.java)
            .equalTo("type", MetricsType.HOST.name).findAll()
        val olds = results.map { it.parentId }.distinct().filter { !hosts.contains(it) }
        realm.executeTransaction {
            for (old in olds) {
                realm.where(UserMetric::class.java)
                    .equalTo("parentId", old)
                    .findAll()
                    .deleteAllFromRealm()
            }
        }
        realm.close()
    }
}
