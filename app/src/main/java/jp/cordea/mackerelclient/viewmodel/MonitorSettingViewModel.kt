package jp.cordea.mackerelclient.viewmodel

import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.MonitorDataResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MonitorSettingViewModel @Inject constructor(
    private val apiClient: MackerelApiClient
) {
    fun deleteMonitorSetting(
        monitor: MonitorDataResponse,
        onResponse: (Response<MonitorDataResponse>?) -> Unit,
        onFailure: () -> Unit
    ) {
        apiClient
            .deleteMonitor(monitor.id)
            .enqueue(object : Callback<MonitorDataResponse> {
                override fun onResponse(p0: Call<MonitorDataResponse>?, response: Response<MonitorDataResponse>?) {
                    onResponse(response)
                }

                override fun onFailure(p0: Call<MonitorDataResponse>?, p1: Throwable?) {
                    onFailure()
                }
            })
    }
}
