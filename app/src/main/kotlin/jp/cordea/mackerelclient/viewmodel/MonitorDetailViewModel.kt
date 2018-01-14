package jp.cordea.mackerelclient.viewmodel

import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Monitor

class MonitorDetailViewModel {

    fun getDisplayData(it: Monitor): List<List<Pair<String, Int>>> =
            arrayListOf<MutableList<Pair<String, Int>>>().apply {
                add(arrayListOf<Pair<String, Int>>().apply {
                    it.name?.let {
                        add(Pair(it, R.string.monitor_detail_name))
                    }
                    it.service?.let {
                        add(Pair(it, R.string.monitor_detail_service))
                    }
                    add(Pair(it.type, R.string.monitor_detail_type))

                    it.duration?.let {
                        add(Pair(it.toString(), R.string.monitor_detail_duration))
                    }
                    it.notificationInterval?.let {
                        add(Pair(it.toString(), R.string.monitor_detail_not_interval))
                    }
                })
                add(arrayListOf<Pair<String, Int>>().apply {
                    it.metric?.let {
                        add(Pair(it, R.string.monitor_detail_metric))
                    }
                    it.operator?.let { op ->
                        it.critical?.let {
                            add(Pair("%s %s".format(op, it.toString()), R.string.monitor_detail_critical))
                        }
                        it.warning?.let {
                            add(Pair("%s %s".format(op, it.toString()), R.string.monitor_detail_warning))
                        }
                    }
                })
                add(arrayListOf<Pair<String, Int>>().apply {
                    if (it.scopes.isNotEmpty()) {
                        add(Pair(it.scopes.joinToString(", "), R.string.monitor_detail_scope))
                    }
                    if (it.excludeScopes.isNotEmpty()) {
                        add(Pair(it.excludeScopes.joinToString(", "), R.string.monitor_detail_ex_scope))
                    }
                })
                add(arrayListOf<Pair<String, Int>>().apply {
                    it.url?.let {
                        add(Pair(it, R.string.monitor_detail_url))
                    }
                })
            }
}
