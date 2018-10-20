package jp.cordea.mackerelclient.repository

import jp.cordea.mackerelclient.api.MackerelApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRemoteDataSource @Inject constructor(
    private val apiClient: MackerelApiClient
) {
    fun getUsers(key: String) = apiClient.getUsers(key)
}
