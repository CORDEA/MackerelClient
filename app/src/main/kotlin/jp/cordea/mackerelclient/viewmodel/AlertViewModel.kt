package jp.cordea.mackerelclient.viewmodel

import android.content.Context
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Alert
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by Yoshihiro Tanaka on 2017/03/24.
 */
class AlertViewModel(private val context: Context) {

    fun getAlerts(
            alerts: List<Alert>?,
            filter: (Alert) -> Boolean = { true }
    ): Observable<List<Alert>> {
        val observable = if (alerts == null) {
            MackerelApiClient
                    .getAlerts(context)
                    .flatMap {
                        Observable.from(it.alerts)
                                .filter(filter)
                    }
        } else {
            Observable.from(alerts)
        }

        return observable
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
    }
}
