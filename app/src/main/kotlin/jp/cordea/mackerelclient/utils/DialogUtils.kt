package jp.cordea.mackerelclient.utils

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import retrofit2.Response
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

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
        Observable
                .create<Unit> {
                    val ad = AlertDialog
                            .Builder(context)
                            .setMessage(title)
                            .create()
                    if (message != 0) {
                    } else {
                        ad.setTitle(title)
                        ad.setMessage(context.resources.getString(message))
                    }

                    ad.show()
                    it.onNext(Unit)
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
