package jp.cordea.mackerelclient.viewmodel

import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.HostDataResponse
import jp.cordea.mackerelclient.api.response.RetireHost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class HostRetireViewModel @Inject constructor(
    private val apiClient: MackerelApiClient
) {
    fun retireHost(
        host: HostDataResponse,
        onResponse: (Response<RetireHost>?) -> Unit,
        onFailure: () -> Unit
    ) {
        apiClient
            .retireHost(host.id)
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
