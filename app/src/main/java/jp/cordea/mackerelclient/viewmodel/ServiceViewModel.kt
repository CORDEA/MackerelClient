package jp.cordea.mackerelclient.viewmodel

import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.Realm
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Services
import jp.cordea.mackerelclient.model.UserMetric
import javax.inject.Inject

class ServiceViewModel @Inject constructor(
    private val apiClient: MackerelApiClient
) {

    fun getServices(): Maybe<Services> =
        apiClient
            .getServices()
            .filter {
                deleteOldMetricData(it.services.map { it.name })
                true
            }
            .observeOn(AndroidSchedulers.mainThread())

    private fun deleteOldMetricData(hosts: List<String>) {
        val realm = Realm.getDefaultInstance()
        val results = realm.where(UserMetric::class.java)
            .equalTo("type", MetricsType.SERVICE.name).findAll()
        realm.executeTransaction {
            val olds = results.map { it.parentId }.distinct().filter { !hosts.contains(it) }
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
