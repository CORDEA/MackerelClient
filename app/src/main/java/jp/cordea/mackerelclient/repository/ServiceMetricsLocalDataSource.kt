package jp.cordea.mackerelclient.repository

import io.realm.Realm
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.model.UserMetric
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceMetricsLocalDataSource @Inject constructor() {
    fun getMetricsDefinition(serviceName: String): List<UserMetric> =
        Realm.getDefaultInstance().use { realm ->
            realm.copyFromRealm(
                realm.where(UserMetric::class.java)
                    .equalTo("type", MetricsType.SERVICE.name)
                    .equalTo("parentId", serviceName).findAll()
            ).sortedBy { it.id }
        }
}
