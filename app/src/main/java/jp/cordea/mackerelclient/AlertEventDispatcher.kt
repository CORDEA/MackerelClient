package jp.cordea.mackerelclient

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.api.response.Alerts
import jp.cordea.mackerelclient.di.ActivityScope
import jp.cordea.mackerelclient.fragment.alert.CriticalAlertFragment
import jp.cordea.mackerelclient.fragment.alert.OtherAlertFragment
import javax.inject.Inject

private const val CRITICAL_STATUS = "CRITICAL"

@ActivityScope
class AlertEventDispatcher @Inject constructor(
) : AlertItemChangedSink,
    AlertResultReceivedSink,
    AlertItemChangedSource,
    AlertResultReceivedSource {
    private val itemChangedSubject = PublishSubject.create<Alerts>()
    private val resultReceivedSubject = PublishSubject.create<Int>()

    override fun notifyAlertItemChanged(alerts: Alerts) = itemChangedSubject.onNext(alerts)

    override fun notifyAlertItemChanged(throwable: Throwable) =
        itemChangedSubject.onError(throwable)

    override fun notifyAlertResultReceived(requestCode: Int) =
        resultReceivedSubject.onNext(requestCode)

    override fun onAlertItemChanged(): Observable<List<Alert>> =
        itemChangedSubject.map { it.alerts }

    override fun onAlertResultReceived(): Observable<Int> = resultReceivedSubject
}

@ActivityScope
class CriticalAlertEventDispatcher @Inject constructor(
    private val dispatcher: AlertEventDispatcher
) : CriticalAlertItemChangedSource,
    CriticalAlertResultReceivedSource {
    override fun onAlertItemChanged(): Observable<List<Alert>> =
        dispatcher.onAlertItemChanged().map { alerts -> alerts.filter { it.status == CRITICAL_STATUS } }

    override fun onAlertResultReceived(): Observable<Int> =
        dispatcher.onAlertResultReceived().filter { it == CriticalAlertFragment.REQUEST_CODE }
}

@ActivityScope
class OtherAlertEventDispatcher @Inject constructor(
    private val dispatcher: AlertEventDispatcher
) : OtherAlertItemChangedSource,
    OtherAlertResultReceivedSource {
    override fun onAlertItemChanged(): Observable<List<Alert>> =
        dispatcher.onAlertItemChanged().map { alerts -> alerts.filter { it.status != CRITICAL_STATUS } }

    override fun onAlertResultReceived(): Observable<Int> =
        dispatcher.onAlertResultReceived().filter { it == OtherAlertFragment.REQUEST_CODE }
}

interface AlertItemChangedSink {
    fun notifyAlertItemChanged(alerts: Alerts)
    fun notifyAlertItemChanged(throwable: Throwable)
}

interface AlertItemChangedSource {
    fun onAlertItemChanged(): Observable<List<Alert>>
}

interface CriticalAlertItemChangedSource : AlertItemChangedSource

interface OtherAlertItemChangedSource : AlertItemChangedSource

interface AlertResultReceivedSink {
    fun notifyAlertResultReceived(requestCode: Int)
}

interface AlertResultReceivedSource {
    fun onAlertResultReceived(): Observable<Int>
}

interface CriticalAlertResultReceivedSource : AlertResultReceivedSource

interface OtherAlertResultReceivedSource : AlertResultReceivedSource
