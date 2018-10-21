package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import dagger.android.support.AndroidSupportInjection
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.model.DisplayableAlert
import jp.cordea.mackerelclient.utils.DialogUtils
import jp.cordea.mackerelclient.viewmodel.AlertCloseViewModel
import javax.inject.Inject

class AlertCloseDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModel: AlertCloseViewModel

    var onSuccess = { }

    private val alert by lazy { arguments!!.getParcelable<DisplayableAlert>(ALERT_KEY) }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null)
        val editText = layout.findViewById(R.id.reason) as EditText
        return AlertDialog.Builder(context)
            .setTitle(R.string.alert_detail_close_dialog_title)
            .setView(layout)
            .setPositiveButton(R.string.alert_detail_close_positive_button) { _, _ ->
                val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
                dialog.show()
                closeAlert(editText.text.toString())
            }
            .create()
    }

    private fun closeAlert(text: String) {
        val context = context ?: return
        viewModel.closeAlert(
            alert,
            text,
            onResponse = {
                dialog.dismiss()
                if (it != null) {
                    val success = DialogUtils.switchDialog(
                        context, it,
                        R.string.alert_detail_error_close_dialog_title,
                        R.string.error_403_dialog_message
                    )
                    if (success) {
                        onSuccess()
                    }
                } else {
                    DialogUtils.showDialog(
                        context,
                        R.string.alert_detail_error_close_dialog_title
                    )
                }
            },
            onFailure = {
                dialog.dismiss()
                DialogUtils.showDialog(
                    context,
                    R.string.alert_detail_error_close_dialog_title
                )
            }
        )
    }

    companion object {
        private const val ALERT_KEY = "AlertKey"

        fun newInstance(alert: DisplayableAlert): AlertCloseDialogFragment =
            AlertCloseDialogFragment().apply {
                arguments = bundleOf(
                    ALERT_KEY to alert
                )
            }
    }
}
