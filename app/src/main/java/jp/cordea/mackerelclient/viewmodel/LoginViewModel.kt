package jp.cordea.mackerelclient.viewmodel

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.realm.Realm
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Users
import jp.cordea.mackerelclient.model.UserKey
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val apiClient: MackerelApiClient
) {

    fun logIn(
        key: String,
        email: String?,
        autoLogin: Boolean,
        onSuccess: (id: Int?) -> Unit,
        onFailure: () -> Unit
    ): Disposable {
        return apiClient
            .getUsers(key)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (autoLogin) {
                    onSuccess(null)
                } else {
                    storeLoginUser(it, key, email, onSuccess, onFailure)
                }
            }, {
                onFailure()
            })
    }

    private fun storeLoginUser(
        it: Users,
        key: String,
        email: String?,
        onSuccess: (id: Int?) -> Unit,
        onFailure: () -> Unit
    ) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val maxId: Number? = realm.where(UserKey::class.java).max("id")
        val user = UserKey()
        val id = (maxId?.toInt() ?: -1) + 1
        user.id = id
        user.key = key

        if (email.isNullOrBlank()) {
            realm.copyToRealm(user)
            realm.commitTransaction()
            realm.close()
            onSuccess(id)
        } else {
            val response = it.users.filter { it.email == email }
            if (response.isEmpty()) {
                realm.cancelTransaction()
                realm.close()
                onFailure()
            } else {
                user.email = response.first().email
                user.name = response.first().screenName
                realm.copyToRealm(user)
                realm.commitTransaction()
                realm.close()
                onSuccess(id)
            }
        }
    }
}
