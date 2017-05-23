package jp.cordea.mackerelclient.api.response

/**
 * Created by Yoshihiro Tanaka on 16/01/12.
 */
class Users(val users: List<User>)

class User(val id: String, val screenName: String, val email: String)