package jp.cordea.mackerelclient.viewmodel

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.realm.Realm
import io.realm.kotlin.createObject
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
        val maxId: Number? = realm.where(UserKey::class.java).max("id")
        val id = (maxId?.toInt() ?: -1) + 1
        if (email.isNullOrBlank()) {
            realm.executeTransaction {
                it.createObject<UserKey>(id).apply { this.key = key }
            }
            realm.close()
            onSuccess(id)
        } else {
            val response = it.users.filter { it.email == email }
            if (response.isEmpty()) {
                realm.close()
                onFailure()
            } else {
                realm.executeTransaction {
                    it.createObject<UserKey>(id).apply {
                        this.key = key
                        this.email = response.first().email
                        this.name = response.first().screenName
                    }
                }
                realm.close()
                onSuccess(id)
            }
        }
    }
}
