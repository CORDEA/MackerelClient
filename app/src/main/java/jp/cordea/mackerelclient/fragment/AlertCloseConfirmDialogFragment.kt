package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.SingleSubject
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.AlertDetailActivity
import jp.cordea.mackerelclient.model.DisplayableAlert

fun AlertDetailActivity.showAlertCloseConfirmDialogFragment(
    alert: DisplayableAlert
): Completable =
    AlertCloseConfirmDialogFragment.newInstance()
        .show(supportFragmentManager)
        .filter { it is AlertCloseConfirmResult.Close }
        .map { it as AlertCloseConfirmResult.Close }
        .flatMapCompletable {
            AlertCloseDialogFragment.newInstance(alert, it.reason)
                .show(supportFragmentManager)
        }
        .subscribeOn(AndroidSchedulers.mainThread())

sealed class AlertCloseConfirmResult {
    class Close(val reason: String) : AlertCloseConfirmResult()
    object Dismiss : AlertCloseConfirmResult()
}

class AlertCloseConfirmDialogFragment : DialogFragment() {

    private var result: AlertCloseConfirmResult = AlertCloseConfirmResult.Dismiss

    private val onDismiss = SingleSubject.create<AlertCloseConfirmResult>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null)
        val editText = layout.findViewById(R.id.reason) as EditText
        return AlertDialog.Builder(context)
            .setTitle(R.string.alert_detail_close_dialog_title)
            .setView(layout)
            .setPositiveButton(R.string.alert_detail_close_positive_button) { _, _ ->
                result = AlertCloseConfirmResult.Close(editText.text.toString())
                dismiss()
            }
            .create()
    }

    override fun dismiss() {
        super.dismiss()
        onDismiss.onSuccess(result)
    }

    fun show(fragmentManager: FragmentManager): Single<AlertCloseConfirmResult> {
        show(fragmentManager, TAG)
        return onDismiss
    }

    companion object {
        private const val TAG = "AlertCloseConfirmDialogFragment"

        fun newInstance() = AlertCloseConfirmDialogFragment()
    }
}
