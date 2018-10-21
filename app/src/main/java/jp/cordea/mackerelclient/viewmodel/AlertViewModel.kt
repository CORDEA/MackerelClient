package jp.cordea.mackerelclient.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import io.reactivex.processors.PublishProcessor
import jp.cordea.mackerelclient.model.DisplayableAlert
import jp.cordea.mackerelclient.repository.AlertRepository
import jp.cordea.mackerelclient.toUiEvent
import javax.inject.Inject

class AlertViewModel : ViewModel() {
    @Inject
    lateinit var repository: AlertRepository

    private val serialDisposable = SerialDisposable()

    private val _items = PublishProcessor.create<List<DisplayableAlert>>()
    val items = _items.toUiEvent()

    fun start() {
        repository.getAlerts { true }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _items.onNext(it)
            }, {
                _items.onError(it)
            })
            .run(serialDisposable::set)
    }

    override fun onCleared() {
        super.onCleared()
        serialDisposable.dispose()
    }
}
