package jp.cordea.mackerelclient.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import io.reactivex.processors.PublishProcessor
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.MonitorDataResponse
import jp.cordea.mackerelclient.toUiEvent
import javax.inject.Inject

class MonitorViewModel : ViewModel() {
    @Inject
    lateinit var apiClient: MackerelApiClient

    private val serialDisposable = SerialDisposable()

    private val _adapterItems = PublishProcessor.create<Map<String, List<MonitorDataResponse>>>()
    val adapterItems = _adapterItems.toUiEvent()
    private val _isRefreshing = PublishProcessor.create<Boolean>()
    val isRefreshing = _isRefreshing.toUiEvent()
    private val _isSwipeRefreshLayoutVisible = PublishProcessor.create<Boolean>()
    val isSwipeRefreshLayoutVisible = _isSwipeRefreshLayoutVisible.toUiEvent()
    private val _isProgressLayoutVisible = PublishProcessor.create<Boolean>()
    val isProgressLayoutVisible = _isProgressLayoutVisible.toUiEvent()
    private val _isErrorVisible = PublishProcessor.create<Boolean>()
    val isErrorVisible = _isErrorVisible.toUiEvent()

    fun start() {
        refresh()
    }

    fun clickedRetryButton() {
        _isProgressLayoutVisible.onNext(true)
        _isErrorVisible.onNext(false)
        refresh()
    }

    fun refresh() {
        _isRefreshing.onNext(true)
        apiClient
            .getMonitors()
            .map { it.monitors }
            .map { list -> list.groupBy { it.type } }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _adapterItems.onNext(it)
                _isRefreshing.onNext(false)
                _isProgressLayoutVisible.onNext(false)
                _isSwipeRefreshLayoutVisible.onNext(true)
            }, {
                _isRefreshing.onNext(false)
                _isErrorVisible.onNext(true)
                _isProgressLayoutVisible.onNext(false)
            })
            .run(serialDisposable::set)
    }

    override fun onCleared() {
        super.onCleared()
        serialDisposable.dispose()
    }
}
