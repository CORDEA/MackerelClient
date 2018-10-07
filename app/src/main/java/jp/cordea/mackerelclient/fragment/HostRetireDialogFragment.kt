package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Host
import jp.cordea.mackerelclient.utils.DialogUtils
import jp.cordea.mackerelclient.viewmodel.HostRetireViewModel

class HostRetireDialogFragment : DialogFragment() {

    var onSuccess = { }

    private val viewModel by lazy {
        HostRetireViewModel(context!!)
    }

    private val host: Host
        get() = arguments!!.getSerializable(HOST_KEY) as Host

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        return AlertDialog.Builder(context)
            .setMessage(R.string.host_detail_retire_dialog_title)
            .setPositiveButton(R.string.retire_positive_button, { _, _ ->
                val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
                dialog.show()
                retireHost()
            })
            .create()
    }

    private fun retireHost() {
        val context = context ?: return
        viewModel.retireHost(
            host,
            onResponse = {
                dialog.dismiss()
                if (it != null) {
                    val success = DialogUtils.switchDialog(
                        context, it,
                        R.string.host_detail_retire_error_dialog_title,
                        R.string.error_403_dialog_message
                    )
                    if (success) {
                        onSuccess()
                    }
                } else {
                    DialogUtils.showDialog(
                        context,
                        R.string.host_detail_retire_error_dialog_title
                    )
                }
            },
            onFailure = {
                dialog.dismiss()
                DialogUtils.showDialog(
                    context,
                    R.string.host_detail_retire_error_dialog_title
                )
            }
        )
    }

    companion object {

        private const val HOST_KEY = "HostKey"

        fun newInstance(host: Host): HostRetireDialogFragment =
            HostRetireDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(HOST_KEY, host)
                }
            }
    }
}
