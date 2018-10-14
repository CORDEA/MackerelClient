package jp.cordea.mackerelclient.repository

import io.reactivex.Single
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.MetricsResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetricsRemoteDataSource @Inject constructor(
    private val apiClient: MackerelApiClient
) {
    fun getMetrics(
        hostId: String,
        name: String,
        from: Long,
        to: Long
    ): Single<MetricsResponse> =
        apiClient.getMetrics(hostId, name, from, to)
}
