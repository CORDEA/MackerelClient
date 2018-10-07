package jp.cordea.mackerelclient.api.response

class Users(val users: List<User>)

class User(val id: String, val screenName: String, val email: String)
