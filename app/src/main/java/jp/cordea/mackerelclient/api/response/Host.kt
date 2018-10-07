package jp.cordea.mackerelclient.api.response

import java.io.Serializable

class Hosts(val hosts: List<Host>)

class RetireHost(val success: Boolean)

class Host(
    var createdAt: Long,
    var id: String,
    var name: String,
    var displayName: String?,
    var status: String,
    var memo: String,
    var roles: Map<String, Array<String>> = mapOf()
) : Serializable
