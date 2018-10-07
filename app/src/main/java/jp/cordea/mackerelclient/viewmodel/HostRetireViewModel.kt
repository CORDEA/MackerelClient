package jp.cordea.mackerelclient.viewmodel

import android.content.Context
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Host
import jp.cordea.mackerelclient.api.response.RetireHost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HostRetireViewModel(private val context: Context) {

    fun retireHost(
        host: Host,
        onResponse: (Response<RetireHost>?) -> Unit,
        onFailure: () -> Unit
    ) {
        MackerelApiClient
            .retireHost(context, host.id)
            .enqueue(object : Callback<RetireHost> {
                override fun onResponse(
                    call: Call<RetireHost>?,
                    response: Response<RetireHost>?
                ) {
                    onResponse(response)
                }

                override fun onFailure(
                    call: Call<RetireHost>?,
                    throwable: Throwable?
                ) {
                    onFailure()
                }
            })
    }
}
