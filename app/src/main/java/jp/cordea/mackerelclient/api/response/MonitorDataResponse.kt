package jp.cordea.mackerelclient.api.response

import java.io.Serializable

class MonitorsResponse(val monitors: List<MonitorDataResponse>)

class MonitorResponse(val monitor: MonitorDataResponse)

class RefreshMonitor(val id: String)

class MonitorDataResponse(
    var id: String,
    var type: String,
    var name: String?,
    var service: String?,
    var duration: Int?,
    var metric: String?,
    var operator: String?,
    var warning: Float?,
    var critical: Float?,
    var notificationInterval: Int?,
    var url: String?,
    scopes: Array<String>?,
    excludeScopes: Array<String>?
) : Serializable {

    val scopes = scopes ?: arrayOf()

    val excludeScopes = excludeScopes ?: arrayOf()
}
