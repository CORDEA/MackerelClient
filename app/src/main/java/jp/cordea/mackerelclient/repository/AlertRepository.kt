package jp.cordea.mackerelclient.repository

import io.reactivex.Observable
import io.reactivex.Single
import jp.cordea.mackerelclient.api.response.Alert
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertRepository @Inject constructor(
    private val remoteDataSource: AlertRemoteDataSource
) {
    fun getAlerts(filter: (Alert) -> Boolean): Single<List<Alert>> =
        remoteDataSource.getAlerts()
            .flatMap {
                Observable
                    .fromIterable(it.alerts)
                    .filter(filter)
                    .toList()
            }
}
