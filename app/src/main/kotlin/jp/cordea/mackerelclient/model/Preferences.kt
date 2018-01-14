package jp.cordea.mackerelclient.model

import android.content.Context
import android.preference.PreferenceManager

class Preferences(context: Context) {

    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    var userId: Int
        get() {
            return pref.getInt(userIdKey, -1)
        }
        set(value) {
            pref.edit().putInt(userIdKey, value).apply()
        }

    fun clear() {
        pref.edit().remove(userIdKey).apply()
    }

    companion object {
        private const val userIdKey = "UserIdKey"
    }
}
