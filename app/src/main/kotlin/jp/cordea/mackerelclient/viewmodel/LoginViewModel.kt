package jp.cordea.mackerelclient.viewmodel

import android.content.Context
import io.realm.Realm
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Users
import jp.cordea.mackerelclient.model.UserKey
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class LoginViewModel(private val context: Context) {

    fun logIn(
            key: String,
            email: String?,
            autoLogin: Boolean,
            onSuccess: (id: Int?) -> Unit,
            onFailure: () -> Unit
    ): Subscription {
        return MackerelApiClient
                .getUsers(context, key)
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
