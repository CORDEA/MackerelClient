package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Monitor
import jp.cordea.mackerelclient.utils.DialogUtils
import jp.cordea.mackerelclient.viewmodel.MonitorSettingViewModel

class MonitorSettingDeleteDialogFragment : DialogFragment() {

    var onSuccess = { }

    private val viewModel by lazy { MonitorSettingViewModel(context!!) }

    private val monitor: Monitor
        get() = arguments!!.getSerializable(MONITOR_KEY) as Monitor

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        return AlertDialog
            .Builder(context)
            .setMessage(R.string.monitor_detail_delete_dialog_title)
            .setPositiveButton(R.string.delete_positive_button) { _, _ ->
                val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
                dialog.show()
                deleteMonitorSetting()
            }
            .create()
    }

    private fun deleteMonitorSetting() {
        val context = context ?: return
        viewModel.deleteMonitorSetting(monitor,
            onResponse = {
                dialog.dismiss()
                if (it != null) {
                    val success = DialogUtils.switchDialog(
                        context, it,
                        R.string.monitor_detail_error_dialog_title,
                        R.string.error_403_dialog_message
                    )
                    if (success) {
                        onSuccess()
                    }
                } else {
                    DialogUtils.showDialog(
                        context,
                        R.string.monitor_detail_error_dialog_title
                    )
                }
            },
            onFailure = {
                dialog.dismiss()
                DialogUtils.showDialog(
                    context,
                    R.string.monitor_detail_error_dialog_title
                )
            }
        )
    }

    companion object {

        private const val MONITOR_KEY = "MonitorKey"

        fun newInstance(monitor: Monitor): MonitorSettingDeleteDialogFragment =
            MonitorSettingDeleteDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MONITOR_KEY, monitor)
                }
            }
    }
}
