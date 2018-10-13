package jp.cordea.mackerelclient.viewmodel

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Alert
import javax.inject.Inject

class AlertViewModel @Inject constructor(
    private val apiClient: MackerelApiClient
) {

    fun getAlerts(
        alerts: List<Alert>?,
        filter: (Alert) -> Boolean = { true }
    ): Single<List<Alert>> {
        val observable = if (alerts == null) {
            apiClient
                .getAlerts()
                .flatMapObservable {
                    Observable
                        .fromIterable(it.alerts)
                        .filter(filter)
                }
        } else {
            Observable.fromIterable(alerts)
        }
        return observable
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
    }
}
