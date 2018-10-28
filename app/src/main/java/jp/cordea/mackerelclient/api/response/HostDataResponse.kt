package jp.cordea.mackerelclient.api.response

class HostsResponse(val hosts: List<HostDataResponse>)

class HostResponse(val host: HostDataResponse)

class RetireHost(val success: Boolean)

class HostDataResponse(
    var createdAt: Long,
    var id: String,
    var name: String,
    var displayName: String?,
    var status: String,
    var memo: String,
    var roles: Map<String, Array<String>> = mapOf()
)
