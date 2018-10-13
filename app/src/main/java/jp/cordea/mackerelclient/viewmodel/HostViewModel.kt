package jp.cordea.mackerelclient.viewmodel

import android.content.Context
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.Realm
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Hosts
import jp.cordea.mackerelclient.api.response.Tsdbs
import jp.cordea.mackerelclient.model.DisplayHostState
import jp.cordea.mackerelclient.model.UserMetric
import javax.inject.Inject

class HostViewModel @Inject constructor(
    private val context: Context,
    private val apiClient: MackerelApiClient
) {

    val displayHostState: List<DisplayHostState>
        get() {
            val realm = Realm.getDefaultInstance()
            initDisplayHostState(realm)
            return realm
                .copyFromRealm(realm.where(DisplayHostState::class.java).findAll())
                .filter { it.isDisplay ?: false }
                .also { realm.close() }
        }

    fun getHosts(items: List<DisplayHostState>): Maybe<Hosts> =
        apiClient
            .getHosts(items.map { it.name })
            .filter {
                deleteOldMetricData(it.hosts.map { it.id })
                true
            }
            .observeOn(AndroidSchedulers.mainThread())

    fun getLatestMetrics(hosts: Hosts): Single<Tsdbs> =
        apiClient
            .getLatestMetrics(
                hosts.hosts.map { it.id },
                arrayListOf(loadavgMetricsKey, cpuMetricsKey, memoryMetricsKey)
            )
            .observeOn(AndroidSchedulers.mainThread())

    private fun initDisplayHostState(realm: Realm) {
        if (realm.where(DisplayHostState::class.java).findAll().size == 0) {
            realm.executeTransaction {
                for (key in context.resources.getStringArray(R.array.setting_host_cell_arr)) {
                    val item = it.createObject(DisplayHostState::class.java, key)
                    item.isDisplay = (key == "standby" || key == "working")
                }
            }
        }
    }

    private fun deleteOldMetricData(hosts: List<String>) {
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

    companion object {
        const val loadavgMetricsKey = "loadavg5"
        const val cpuMetricsKey = "cpu.user.percentage"
        const val memoryMetricsKey = "memory.used"
    }
}
