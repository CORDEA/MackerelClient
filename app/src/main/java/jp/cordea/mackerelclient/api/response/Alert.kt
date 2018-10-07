package jp.cordea.mackerelclient.api.response

import java.io.Serializable

class Alerts(val alerts: List<Alert>)

class CloseAlert(val reason: String)

class Alert(
    val id: String,
    val status: String,
    val monitorId: String,
    val type: String,
    val hostId: String,
    val value: Float?,
    val message: String?,
    val reason: String?,
    val openedAt: Long,
    val closedAt: Long?
) : Serializable