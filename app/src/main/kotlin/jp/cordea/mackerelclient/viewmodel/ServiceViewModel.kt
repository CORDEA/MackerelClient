package jp.cordea.mackerelclient.viewmodel

import android.content.Context
import io.realm.Realm
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Services
import jp.cordea.mackerelclient.model.UserMetric
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by Yoshihiro Tanaka on 2017/03/29.
 */
class ServiceViewModel(private val context: Context) {

    fun getServices(): Observable<Services> {
        return MackerelApiClient
                .getServices(context)
                .filter {
                    deleteOldMetricData(it.services.map { it.name })
                    true
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

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
