package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import jp.cordea.mackerelclient.HostRetireViewModel
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Host
import jp.cordea.mackerelclient.utils.DialogUtils

/**
 * Created by Yoshihiro Tanaka on 2017/03/22.
 */
class HostRetireDialogFragment(private val host: Host) : DialogFragment() {

    var onSuccess = { }

    private val viewModel by lazy {
        HostRetireViewModel(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                return AlertDialog .Builder(context)
                        .setMessage(R.string.host_detail_retire_dialog_title)
                        .setPositiveButton(R.string.retire_positive_button, { _, _ ->
                            val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
                            dialog.show()
                            viewModel.retireHost(host,
                                    onResponse = {
                                        dialog.dismiss()
                                        it?.let {
                                            val success = DialogUtils.switchDialog(context, it,
                                                    R.string.host_detail_retire_error_dialog_title,
                                                    R.string.error_403_dialog_message)
                                            if (success) {
                                                onSuccess()
                                            }
                                            return@retireHost
                                        }
                                        DialogUtils.showDialog(context, R.string.host_detail_retire_error_dialog_title)
                                    },
                                    onFailure = {
                                        dialog.dismiss()
                                        DialogUtils.showDialog(context, R.string.host_detail_retire_error_dialog_title)
                                    }
                            )
                        })
                        .create()
    }

    companion object {
        fun newInstance(host: Host): HostRetireDialogFragment {
            return HostRetireDialogFragment(host)
        }
    }

}