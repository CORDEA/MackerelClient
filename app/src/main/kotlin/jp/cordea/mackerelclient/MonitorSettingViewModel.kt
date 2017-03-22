package jp.cordea.mackerelclient

import android.content.Context
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Monitor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Yoshihiro Tanaka on 2017/03/22.
 */
class MonitorSettingViewModel(private val context: Context) {

    fun deleteMonitorSetting(monitor: Monitor, onResponse: (response: Response<Monitor>?) -> Unit, onFailure: () -> Unit) {
        MackerelApiClient
                .deleteMonitor(context, monitor.id!!)
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