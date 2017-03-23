package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import jp.cordea.mackerelclient.viewmodel.AlertCloseViewModel
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.utils.DialogUtils

/**
 * Created by Yoshihiro Tanaka on 2017/03/22.
 */
class AlertCloseDialogFragment(private val alert: Alert) : DialogFragment() {

    var onSuccess = { }

    private val viewModel by lazy {
        AlertCloseViewModel(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null)
        val editText = layout.findViewById(R.id.reason) as EditText
        return AlertDialog.Builder(context)
                .setTitle(R.string.alert_detail_close_dialog_title)
                .setView(layout)
                .setPositiveButton(R.string.alert_detail_close_positive_button, { _, _ ->
                    val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
                    dialog.show()
                    viewModel.closeAlert(alert, editText.text.toString(),
                            onResponse = {
                                dialog.dismiss()
                                it?.let {
                                    val success = DialogUtils.switchDialog(context, it,
                                            R.string.alert_detail_error_close_dialog_title,
                                            R.string.error_403_dialog_message)
                                    if (success) {
                                        onSuccess()
                                    }
                                    return@closeAlert
                                }
                                DialogUtils.showDialog(context,
                                        R.string.alert_detail_error_close_dialog_title)
                            },
                            onFailure = {
                                dialog.dismiss()
                                DialogUtils.showDialog(context,
                                        R.string.alert_detail_error_close_dialog_title)
                            }
                    )
                })
                .create()
    }

    companion object {
        fun newInstance(alert: Alert): AlertCloseDialogFragment {
            return AlertCloseDialogFragment(alert)
        }
    }

}