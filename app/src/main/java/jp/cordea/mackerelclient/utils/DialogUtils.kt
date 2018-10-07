package jp.cordea.mackerelclient.utils

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.Response

object DialogUtils {
    fun <T> switchDialog(context: Context, response: Response<T>, title: Int, m403: Int): Boolean {
        if (!response.isSuccessful) {
            when (response.code()) {
                403 ->
                    DialogUtils.showDialog(context, title, m403)
                else ->
                    DialogUtils.showDialog(context, title)
            }
            return false
        }
        return true
    }

    fun showDialog(context: Context, title: Int, message: Int = 0) {
        Single
            .create<Unit> {
                val dialog = AlertDialog
                    .Builder(context)
                    .setMessage(title)
                    .create()
                if (message != 0) {
                } else {
                    dialog.setTitle(title)
                    dialog.setMessage(context.resources.getString(message))
                }

                dialog.show()
                it.onSuccess(Unit)
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun progressDialog(context: Context, title: Int, message: Int = 0): ProgressDialog {
        val progress = ProgressDialog(context)
        if (message == 0) {
            progress.setMessage(context.resources.getString(title))
        } else {
            progress.setTitle(context.resources.getString(title))
            progress.setMessage(context.resources.getString(message))
        }
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.setCancelable(false)
        progress.setCanceledOnTouchOutside(false)
        return progress
    }
}
