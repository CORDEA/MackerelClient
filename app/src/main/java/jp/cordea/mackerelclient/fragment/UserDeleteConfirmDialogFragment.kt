package jp.cordea.mackerelclient.fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import dagger.android.support.AndroidSupportInjection
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.UserDeleteConfirmSink
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.User
import jp.cordea.mackerelclient.utils.DialogUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class UserDeleteConfirmDialogFragment : DialogFragment() {

    @Inject
    lateinit var apiClient: MackerelApiClient

    @Inject
    lateinit var confirmSink: UserDeleteConfirmSink

    private val id get() = arguments!!.getString(ID_KEY)!!

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        return AlertDialog
            .Builder(context)
            .setMessage(R.string.user_delete_dialog_title)
            .setPositiveButton(R.string.delete_positive_button) { _, _ ->
                val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
                dialog.show()
                deleteUser(dialog)
            }
            .create()
    }

    private fun deleteUser(dialog: ProgressDialog) {
        val context = context!!
        apiClient
            .deleteUser(id)
            .enqueue(object : Callback<User> {
                override fun onResponse(p0: Call<User>?, response: Response<User>?) {
                    dialog.dismiss()
                    response?.let { resp ->
                        val success = DialogUtils.switchDialog(
                            context,
                            resp,
                            R.string.user_delete_error_dialog_title,
                            R.string.error_403_dialog_message
                        )
                        if (success) {
                            confirmSink.notifyUserDeleteCompleted()
                        }
                        return
                    }
                    DialogUtils.showDialog(
                        context,
                        R.string.user_delete_error_dialog_title
                    )
                }

                override fun onFailure(p0: Call<User>?, p1: Throwable?) {
                    dialog.dismiss()
                    DialogUtils.showDialog(
                        context,
                        R.string.user_delete_error_dialog_title
                    )
                }
            })
    }

    companion object {
        const val TAG = "UserDeleteConfirmDialogFragment"
        private const val ID_KEY = "IdKey"

        fun newInstance(id: String) = UserDeleteConfirmDialogFragment().apply {
            arguments = bundleOf(
                ID_KEY to id
            )
        }
    }
}
