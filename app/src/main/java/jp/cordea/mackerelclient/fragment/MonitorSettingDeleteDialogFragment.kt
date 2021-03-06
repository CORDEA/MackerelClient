package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import dagger.android.support.AndroidSupportInjection
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.MonitorDataResponse
import jp.cordea.mackerelclient.utils.DialogUtils
import jp.cordea.mackerelclient.viewmodel.MonitorSettingViewModel
import javax.inject.Inject

class MonitorSettingDeleteDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModel: MonitorSettingViewModel

    var onSuccess = { }

    private val monitor: MonitorDataResponse
        get() = arguments!!.getSerializable(MONITOR_KEY) as MonitorDataResponse

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        return AlertDialog
            .Builder(context)
            .setMessage(R.string.monitor_detail_delete_dialog_title)
            .setPositiveButton(R.string.delete_positive_button) { _, _ ->
                val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
                dialog.show()
                deleteMonitorSetting(dialog)
            }
            .create()
    }

    private fun deleteMonitorSetting(dialog: ProgressDialog) {
        val context = context ?: return
        viewModel.deleteMonitorSetting(
            monitor,
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

        fun newInstance(monitor: MonitorDataResponse): MonitorSettingDeleteDialogFragment =
            MonitorSettingDeleteDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MONITOR_KEY, monitor)
                }
            }
    }
}
