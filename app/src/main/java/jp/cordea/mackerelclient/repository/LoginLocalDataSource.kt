package jp.cordea.mackerelclient.repository

import io.reactivex.Single
import io.realm.Realm
import io.realm.kotlin.createObject
import jp.cordea.mackerelclient.api.response.Users
import jp.cordea.mackerelclient.model.UserKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginLocalDataSource @Inject constructor() {
    fun getLoginUser(userId: Int): UserKey? =
        Realm.getDefaultInstance().use {
            val key = it.where(UserKey::class.java).equalTo("id", userId).findFirst()
            if (key == null) {
                null
            } else {
                it.copyFromRealm(key)
            }
        }

    fun storeLoginUser(
        users: Users,
        key: String,
        email: String?
    ): Single<Int> =
        Single.create<Int> { emitter ->
            val realm = Realm.getDefaultInstance()
            val maxId: Number? = realm.where(UserKey::class.java).max("id")
            val id = (maxId?.toInt() ?: -1) + 1
            if (email.isNullOrBlank()) {
                realm.executeTransaction {
                    it.createObject<UserKey>(id).apply { this.key = key }
                }
                realm.close()
                emitter.onSuccess(id)
            } else {
                val response = users.users.filter { it.email == email }
                if (response.isEmpty()) {
                    realm.close()
                    emitter.onError(WrongEmailException())
                } else {
                    val first = response.first()
                    realm.executeTransaction {
                        it.createObject<UserKey>(id).apply {
                            this.key = key
                            this.email = first.email
                            this.name = first.screenName
                        }
                    }
                    realm.close()
                    emitter.onSuccess(id)
                }
            }
        }
}
