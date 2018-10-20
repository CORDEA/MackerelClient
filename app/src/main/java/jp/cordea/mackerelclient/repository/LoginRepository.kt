package jp.cordea.mackerelclient.repository

import io.reactivex.Single
import jp.cordea.mackerelclient.api.response.Users
import jp.cordea.mackerelclient.model.UserKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val remoteDataSource: LoginRemoteDataSource,
    private val localDataSource: LoginLocalDataSource
) {
    fun getLoginUser(userId: Int): UserKey? = localDataSource.getLoginUser(userId)

    fun autoLogin(key: String): Single<Users> =
        remoteDataSource.getUsers(key)

    fun login(
        key: String,
        email: String?
    ): Single<Int> = remoteDataSource.getUsers(key)
        .flatMap { localDataSource.storeLoginUser(it, key, email) }
}
