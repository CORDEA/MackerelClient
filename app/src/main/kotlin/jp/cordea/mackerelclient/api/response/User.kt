package jp.cordea.mackerelclient.api.response

/**
 * Created by Yoshihiro Tanaka on 16/01/12.
 */
class Users(val users: List<User>)

class User(id: String, screenName: String, email: String) {
    val id = id
    val screenName = screenName
    val email = email
}