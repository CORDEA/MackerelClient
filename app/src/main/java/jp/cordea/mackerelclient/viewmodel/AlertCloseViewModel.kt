package jp.cordea.mackerelclient.viewmodel

import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.AlertDataResponse
import jp.cordea.mackerelclient.api.response.CloseAlert
import jp.cordea.mackerelclient.model.DisplayableAlert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AlertCloseViewModel @Inject constructor(
    private val apiClient: MackerelApiClient
) {
    fun closeAlert(
        alert: DisplayableAlert,
        reason: String,
        onResponse: (Response<AlertDataResponse>?) -> Unit,
        onFailure: () -> Unit
    ) {
        apiClient
            .closeAlert(alert.id, CloseAlert(reason))
            .enqueue(object : Callback<AlertDataResponse> {
                override fun onResponse(p0: Call<AlertDataResponse>?, response: Response<AlertDataResponse>?) {
                    onResponse(response)
                }

                override fun onFailure(p0: Call<AlertDataResponse>?, p1: Throwable?) {
                    onFailure()
                }
            })
    }
}
