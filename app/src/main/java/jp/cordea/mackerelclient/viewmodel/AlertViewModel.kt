package jp.cordea.mackerelclient.viewmodel

import android.content.Context
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Alert

class AlertViewModel(private val context: Context) {

    fun getAlerts(
        alerts: List<Alert>?,
        filter: (Alert) -> Boolean = { true }
    ): Single<List<Alert>> {
        val observable = if (alerts == null) {
            MackerelApiClient
                .getAlerts(context)
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
