package jp.cordea.mackerelclient.viewmodel

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.View
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.User
import jp.cordea.mackerelclient.utils.DialogUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserListItemViewModel(private val context: Context, private val item: User) {

    var onUserDeleteSucceeded = { }

    val deleteButtonOnClick = View.OnClickListener {
        AlertDialog
            .Builder(context)
            .setMessage(R.string.user_delete_dialog_title)
            .setPositiveButton(R.string.delete_positive_button) { _, _ ->
                val dialog = DialogUtils.progressDialog(context, R.string.progress_dialog_title)
                dialog.show()
                deleteUser(dialog)
            }
            .show()
    }

    private fun deleteUser(dialog: ProgressDialog) {
        MackerelApiClient
            .deleteUser(context, item.id)
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
                            onUserDeleteSucceeded()
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
}
