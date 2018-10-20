package jp.cordea.mackerelclient.repository

import io.reactivex.Single
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Hosts
import jp.cordea.mackerelclient.api.response.Tsdbs
import jp.cordea.mackerelclient.model.DisplayHostState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HostRemoteDataSource @Inject constructor(
    private val apiClient: MackerelApiClient
) {
    fun getHosts(items: List<DisplayHostState>): Single<Hosts> =
        apiClient.getHosts(items.map { it.name })

    fun getLatestMetrics(
        hosts: Hosts,
        names: List<String>
    ): Single<Tsdbs> =
        apiClient
            .getLatestMetrics(
                hosts.hosts.map { it.id },
                names
            )
}
