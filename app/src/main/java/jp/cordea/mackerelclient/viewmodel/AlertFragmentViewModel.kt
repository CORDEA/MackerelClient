package jp.cordea.mackerelclient.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.processors.PublishProcessor
import jp.cordea.mackerelclient.api.response.AlertDataResponse
import jp.cordea.mackerelclient.model.DisplayableAlert
import jp.cordea.mackerelclient.repository.AlertRepository
import jp.cordea.mackerelclient.toUiEvent
import javax.inject.Inject

class AlertFragmentViewModel : ViewModel() {
    @Inject
    lateinit var repository: AlertRepository

    private lateinit var filter: (AlertDataResponse) -> Boolean

    private var alerts: List<DisplayableAlert>? = null

    private val compositeDisposable = CompositeDisposable()
    private val serialDisposable = SerialDisposable()

    private val _adapterItems = PublishProcessor.create<List<DisplayableAlert>>()
    val adapterItems = _adapterItems.toUiEvent()
    private val _isRefreshing = PublishProcessor.create<Boolean>()
    val isRefreshing = _isRefreshing.toUiEvent()
    private val _isSwipeRefreshLayoutVisible = PublishProcessor.create<Boolean>()
    val isSwipeRefreshLayoutVisible = _isSwipeRefreshLayoutVisible.toUiEvent()
    private val _isProgressLayoutVisible = PublishProcessor.create<Boolean>()
    val isProgressLayoutVisible = _isProgressLayoutVisible.toUiEvent()
    private val _isErrorVisible = PublishProcessor.create<Boolean>()
    val isErrorVisible = _isErrorVisible.toUiEvent()

    fun start(filter: (AlertDataResponse) -> Boolean) {
        this.filter = filter
    }

    fun clickedRetryButton() {
        _isProgressLayoutVisible.onNext(true)
        _isErrorVisible.onNext(false)
        refresh(true)
    }

    fun refresh(forceRefresh: Boolean) {
        _isRefreshing.onNext(true)
        if (alerts != null && !forceRefresh) {
            Single.just(alerts)
        } else {
            repository.getAlerts(filter)
                .doOnSuccess { alerts = it }
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _adapterItems.onNext(it)
                _isRefreshing.onNext(false)
                _isSwipeRefreshLayoutVisible.onNext(true)
                _isProgressLayoutVisible.onNext(false)
            }, {
                _isRefreshing.onNext(false)
                _isErrorVisible.onNext(true)
                _isProgressLayoutVisible.onNext(false)
            })
            .run(serialDisposable::set)
    }

    fun updateCache(alerts: List<DisplayableAlert>) {
        this.alerts = alerts
    }

    fun clearCache() {
        this.alerts = null
    }

    override fun onCleared() {
        super.onCleared()
        serialDisposable.dispose()
        compositeDisposable.clear()
    }
}
