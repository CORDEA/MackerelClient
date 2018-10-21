package jp.cordea.mackerelclient.viewmodel

import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.MonitorDataResponse

class MonitorDetailViewModel {

    fun getDisplayData(it: MonitorDataResponse): List<List<Pair<String, Int>>> =
        arrayListOf<MutableList<Pair<String, Int>>>().apply {
            add(arrayListOf<Pair<String, Int>>().apply {
                it.name?.let {
                    add(it to R.string.monitor_detail_name)
                }
                it.service?.let {
                    add(it to R.string.monitor_detail_service)
                }
                add(it.type to R.string.monitor_detail_type)

                it.duration?.let {
                    add(it.toString() to R.string.monitor_detail_duration)
                }
                it.notificationInterval?.let {
                    add(it.toString() to R.string.monitor_detail_not_interval)
                }
            })
            add(arrayListOf<Pair<String, Int>>().apply {
                it.metric?.let {
                    add(it to R.string.monitor_detail_metric)
                }
                it.operator?.let { op ->
                    it.critical?.let {
                        add(
                            "%s %s".format(op, it.toString()) to R.string.monitor_detail_critical
                        )
                    }
                    it.warning?.let {
                        add(
                            "%s %s".format(op, it.toString()) to R.string.monitor_detail_warning
                        )
                    }
                }
            })
            add(arrayListOf<Pair<String, Int>>().apply {
                if (it.scopes.isNotEmpty()) {
                    add(it.scopes.joinToString(", ") to R.string.monitor_detail_scope)
                }
                if (it.excludeScopes.isNotEmpty()) {
                    add(it.excludeScopes.joinToString(", ") to R.string.monitor_detail_ex_scope)
                }
            })
            add(arrayListOf<Pair<String, Int>>().apply {
                it.url?.let {
                    add(it to R.string.monitor_detail_url)
                }
            })
        }
}
