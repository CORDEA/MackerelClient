package jp.cordea.mackerelclient.api.response

import java.io.Serializable

class AlertsResponse(val alerts: List<AlertDataResponse>)

class CloseAlert(val reason: String)

class AlertDataResponse(
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
