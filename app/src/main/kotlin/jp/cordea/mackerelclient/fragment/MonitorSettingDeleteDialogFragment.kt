package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Monitor
import jp.cordea.mackerelclient.utils.DialogUtils
import jp.cordea.mackerelclient.viewmodel.MonitorSettingViewModel

class MonitorSettingDeleteDialogFragment(private val monitor: Monitor) : DialogFragment() {

    var onSuccess = { }

    private val viewModel by lazy {
        MonitorSettingViewModel(context!!)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        return AlertDialog
                .Builder(context)
                .setMessage(R.string.monitor_detail_delete_dialog_title)
                .setPositiveButton(R.string.delete_positive_button, { _, _ ->
                    val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
                    dialog.show()
                    viewModel.deleteMonitorSetting(monitor,
                            onResponse = {
                                dialog.dismiss()
                                it?.let {
                                    val success = DialogUtils.switchDialog(context, it,
                                            R.string.monitor_detail_error_dialog_title,
                                            R.string.error_403_dialog_message)
                                    if (success) {
                                        onSuccess()
                                    }
                                    return@deleteMonitorSetting
                                }
                                DialogUtils.showDialog(context,
                                        R.string.monitor_detail_error_dialog_title)
                            },
                            onFailure = {
                                dialog.dismiss()
                                DialogUtils.showDialog(context,
                                        R.string.monitor_detail_error_dialog_title)
                            }
                    )
                })
                .create()
    }

    companion object {
        fun newInstance(monitor: Monitor): MonitorSettingDeleteDialogFragment {
            return MonitorSettingDeleteDialogFragment(monitor)
        }
    }

}
