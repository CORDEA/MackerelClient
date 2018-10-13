package jp.cordea.mackerelclient.viewmodel

import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.api.response.CloseAlert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AlertCloseViewModel @Inject constructor(
    private val apiClient: MackerelApiClient
) {
    fun closeAlert(
        alert: Alert,
        reason: String,
        onResponse: (Response<Alert>?) -> Unit,
        onFailure: () -> Unit
    ) {
        apiClient
            .closeAlert(alert.id, CloseAlert(reason))
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
