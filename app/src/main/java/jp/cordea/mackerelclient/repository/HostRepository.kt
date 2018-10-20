package jp.cordea.mackerelclient.repository

import io.reactivex.Single
import jp.cordea.mackerelclient.api.response.Hosts
import jp.cordea.mackerelclient.api.response.Tsdbs
import jp.cordea.mackerelclient.model.DisplayHostState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HostRepository @Inject constructor(
    private val remoteDataSource: HostRemoteDataSource,
    private val localDataSource: HostLocalDataSource
) {
    fun getHosts(items: List<DisplayHostState>): Single<Hosts> =
        remoteDataSource.getHosts(items)

    fun getLatestMetrics(
        hosts: Hosts,
        names: List<String>
    ): Single<Tsdbs> = remoteDataSource.getLatestMetrics(hosts, names)

    fun getDisplayHostStates(defaults: Array<String>): List<DisplayHostState> =
        localDataSource.getDisplayHostStates(defaults)

    fun deleteOldMetrics(hosts: List<String>) = localDataSource.deleteOldMetrics(hosts)
}
