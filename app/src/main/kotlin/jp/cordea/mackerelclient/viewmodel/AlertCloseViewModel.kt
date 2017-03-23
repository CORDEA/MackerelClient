package jp.cordea.mackerelclient.viewmodel

import android.content.Context
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.api.response.CloseAlert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Yoshihiro Tanaka on 2017/03/22.
 */
class AlertCloseViewModel(private val context: Context) {

    fun closeAlert(alert: Alert, reason: String, onResponse: (response: Response<Alert>?) -> Unit, onFailure: () -> Unit) {
        MackerelApiClient
                .closeAlert(context, alert.id!!, CloseAlert(reason))
                .enqueue(object : Callback<Alert> {
                    override fun onResponse(p0: Call<Alert>?, response: Response<Alert>?) {
                        onResponse(response)
                    }

                    override fun onFailure(p0: Call<Alert>?, p1: Throwable?) {
                        onFailure()
                    }
                })
    }
}