package jp.cordea.mackerelclient.utils

import android.app.ProgressDialog
import android.content.Context
import com.pawegio.kandroid.alert
import retrofit2.Response
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by CORDEA on 2016/01/23.
 */
class DialogUtils {
    companion object {
        public fun <T> switchDialog(context: Context, response: Response<T>, title: Int, m403: Int) : Boolean {
            if (!response.isSuccess) {
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

        public fun showDialog(context: Context, title: Int, message: Int = 0) {
            Observable
                    .create<Unit> {
                        context.alert {
                            if (message == 0) {
                                message(title)
                            } else {
                                title(title)
                                message(message)
                            }
                        }
                        .show()
                        it.onNext(Unit)
                    }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }

        public fun progressDialog(context: Context, title: Int, message: Int = 0): ProgressDialog {
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
}