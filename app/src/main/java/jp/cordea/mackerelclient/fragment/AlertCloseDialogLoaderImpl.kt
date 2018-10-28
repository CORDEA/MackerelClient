package jp.cordea.mackerelclient.fragment

import androidx.fragment.app.FragmentManager
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import jp.cordea.mackerelclient.di.ActivityScope
import jp.cordea.mackerelclient.model.DisplayableAlert
import jp.cordea.mackerelclient.toUiEvent
import javax.inject.Inject

interface AlertCloseDialogLoader {
    val closeAlertCloseDialog: Flowable<AlertCloseResult>
    fun show(alert: DisplayableAlert)
}

interface AlertCloseDialogListener {
    fun onNext(result: AlertCloseConfirmResult)
    fun onNext(result: AlertCloseResult)
}

sealed class AlertCloseConfirmResult {
    class Confirm(
        val alert: DisplayableAlert,
        val reason: String
    ) : AlertCloseConfirmResult()
}

sealed class AlertCloseResult {
    object Success : AlertCloseResult()
    class Error(val throwable: Throwable) : AlertCloseResult()
}

@ActivityScope
class AlertCloseDialogLoaderImpl @Inject constructor(
    private val fragmentManager: FragmentManager
) : AlertCloseDialogLoader, AlertCloseDialogListener {
    private val _closeAlertCloseDialog = PublishProcessor.create<AlertCloseResult>()
    override val closeAlertCloseDialog = _closeAlertCloseDialog.toUiEvent()

    override fun show(alert: DisplayableAlert) {
        AlertCloseConfirmDialogFragment
            .newInstance(alert)
            .show(fragmentManager)
    }

    override fun onNext(result: AlertCloseConfirmResult) {
        when (result) {
            is AlertCloseConfirmResult.Confirm ->
                AlertCloseDialogFragment
                    .newInstance(result.alert, result.reason)
                    .show(fragmentManager)
        }
    }

    override fun onNext(result: AlertCloseResult) {
        _closeAlertCloseDialog.onNext(result)
    }
}
