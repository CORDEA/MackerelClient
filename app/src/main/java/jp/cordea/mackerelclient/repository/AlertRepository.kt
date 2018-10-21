package jp.cordea.mackerelclient.repository

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import jp.cordea.mackerelclient.api.response.AlertDataResponse
import jp.cordea.mackerelclient.model.DisplayableAlert
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertRepository @Inject constructor(
    private val remoteDataSource: AlertRemoteDataSource
) {
    fun getAlerts(filter: (AlertDataResponse) -> Boolean): Single<List<DisplayableAlert>> =
        remoteDataSource.getAlerts()
            .flatMap { alerts ->
                Observable
                    .fromIterable(alerts.alerts)
                    .filter(filter)
                    .toList()
                    .flatMap { getDisplayableAlerts(it) }
            }

    private fun getDisplayableAlerts(alerts: List<AlertDataResponse>): Single<List<DisplayableAlert>> {
        val hostIds = alerts.asSequence().map { it.hostId }.distinct().toList()
        val monitorIds = alerts.asSequence().map { it.monitorId }.distinct().toList()
        return Singles
            .zip(
                Observable.fromIterable(hostIds).flatMapSingle {
                    remoteDataSource.getHost(it)
                }.map { it.host }.toList(),
                Observable.fromIterable(monitorIds).flatMapSingle {
                    remoteDataSource.getMonitor(it)
                }.map { it.monitor }.toList()
            ) { host, monitor ->
                alerts.map { alert ->
                    DisplayableAlert.from(
                        alert,
                        host.firstOrNull { alert.hostId == it.id },
                        monitor.first { alert.monitorId == it.id }
                    )
                }
            }
    }
}
