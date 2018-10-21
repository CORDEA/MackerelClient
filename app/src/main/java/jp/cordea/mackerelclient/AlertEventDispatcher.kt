package jp.cordea.mackerelclient

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import jp.cordea.mackerelclient.di.ActivityScope
import jp.cordea.mackerelclient.fragment.alert.CriticalAlertFragment
import jp.cordea.mackerelclient.fragment.alert.OtherAlertFragment
import jp.cordea.mackerelclient.model.DisplayableAlert
import javax.inject.Inject

private const val CRITICAL_STATUS = "CRITICAL"

@ActivityScope
class AlertEventDispatcher @Inject constructor(
) : AlertItemChangedSink,
    AlertResultReceivedSink,
    AlertItemChangedSource,
    AlertResultReceivedSource {
    private val itemChangedSubject = PublishSubject.create<List<DisplayableAlert>>()
    private val resultReceivedSubject = PublishSubject.create<Int>()

    override fun notifyAlertItemChanged(alerts: List<DisplayableAlert>) =
        itemChangedSubject.onNext(alerts)

    override fun notifyAlertItemChanged(throwable: Throwable) =
        itemChangedSubject.onError(throwable)

    override fun notifyAlertResultReceived(requestCode: Int) =
        resultReceivedSubject.onNext(requestCode)

    override fun onAlertItemChanged(): Observable<List<DisplayableAlert>> =
        itemChangedSubject

    override fun onAlertResultReceived(): Observable<Int> = resultReceivedSubject
}

@ActivityScope
class CriticalAlertEventDispatcher @Inject constructor(
    private val dispatcher: AlertEventDispatcher
) : CriticalAlertItemChangedSource,
    CriticalAlertResultReceivedSource {
    override fun onAlertItemChanged(): Observable<List<DisplayableAlert>> =
        dispatcher.onAlertItemChanged().map { alerts -> alerts.filter { it.status == CRITICAL_STATUS } }

    override fun onAlertResultReceived(): Observable<Int> =
        dispatcher.onAlertResultReceived().filter { it == CriticalAlertFragment.REQUEST_CODE }
}

@ActivityScope
class OtherAlertEventDispatcher @Inject constructor(
    private val dispatcher: AlertEventDispatcher
) : OtherAlertItemChangedSource,
    OtherAlertResultReceivedSource {
    override fun onAlertItemChanged(): Observable<List<DisplayableAlert>> =
        dispatcher.onAlertItemChanged().map { alerts -> alerts.filter { it.status != CRITICAL_STATUS } }

    override fun onAlertResultReceived(): Observable<Int> =
        dispatcher.onAlertResultReceived().filter { it == OtherAlertFragment.REQUEST_CODE }
}

interface AlertItemChangedSink {
    fun notifyAlertItemChanged(alerts: List<DisplayableAlert>)
    fun notifyAlertItemChanged(throwable: Throwable)
}

interface AlertItemChangedSource {
    fun onAlertItemChanged(): Observable<List<DisplayableAlert>>
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
