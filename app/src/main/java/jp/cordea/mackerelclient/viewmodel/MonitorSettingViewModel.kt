package jp.cordea.mackerelclient.viewmodel

import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Monitor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MonitorSettingViewModel @Inject constructor(
    private val apiClient: MackerelApiClient
) {
    fun deleteMonitorSetting(
        monitor: Monitor,
        onResponse: (Response<Monitor>?) -> Unit,
        onFailure: () -> Unit
    ) {
        apiClient
            .deleteMonitor(monitor.id)
            .enqueue(object : Callback<Monitor> {
                override fun onResponse(p0: Call<Monitor>?, response: Response<Monitor>?) {
                    onResponse(response)
                }

                override fun onFailure(p0: Call<Monitor>?, p1: Throwable?) {
                    onFailure()
                }
            })
    }
}
