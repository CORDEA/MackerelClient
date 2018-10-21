package jp.cordea.mackerelclient.model

import android.os.Parcelable
import jp.cordea.mackerelclient.api.response.AlertDataResponse
import jp.cordea.mackerelclient.api.response.HostDataResponse
import jp.cordea.mackerelclient.api.response.MonitorDataResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
class DisplayableAlert(
    val id: String,
    val status: String,
    val monitorId: String,
    val type: String,
    val hostId: String,
    val value: Float?,
    val message: String?,
    val reason: String?,
    val openedAt: Long,
    val closedAt: Long?,
    val hostName: String?,
    val hostMemo: String?,
    val monitorName: String?,
    val critical: Float?,
    val warning: Float?,
    val operator: String?
) : Parcelable {
    companion object {
        fun from(alert: AlertDataResponse, host: HostDataResponse?, monitor: MonitorDataResponse) =
            DisplayableAlert(
                alert.id,
                alert.status,
                alert.monitorId,
                alert.type,
                alert.hostId,
                alert.value,
                alert.message,
                alert.reason,
                alert.openedAt,
                alert.closedAt,
                host?.name,
                host?.memo,
                monitor.name,
                monitor.critical,
                monitor.warning,
                monitor.operator
            )
    }
}
