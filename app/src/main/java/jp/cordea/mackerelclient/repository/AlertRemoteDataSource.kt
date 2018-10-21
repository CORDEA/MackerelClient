package jp.cordea.mackerelclient.repository

import jp.cordea.mackerelclient.api.MackerelApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertRemoteDataSource @Inject constructor(
    private val apiClient: MackerelApiClient
) {
    fun getAlerts() = apiClient.getAlerts()

    fun getHost(id: String) = apiClient.getHost(id)

    fun getMonitor(id: String) = apiClient.getMonitor(id)
}
