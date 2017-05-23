package jp.cordea.mackerelclient.viewmodel

import android.content.Context
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Host
import jp.cordea.mackerelclient.api.response.RetireHost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Yoshihiro Tanaka on 2017/03/22.
 */
class HostRetireViewModel(private val context: Context) {

    fun retireHost(host: Host, onResponse: (response: Response<RetireHost>?) -> Unit, onFailure: () -> Unit) {
        MackerelApiClient
                .retireHost(context, host.id)
                .enqueue(object : Callback<RetireHost> {
                    override fun onResponse(p0: Call<RetireHost>?, response: Response<RetireHost>?) {
                        onResponse(response)
                    }

                    override fun onFailure(p0: Call<RetireHost>?, p1: Throwable?) {
                        onFailure()
                    }
                })
    }

}