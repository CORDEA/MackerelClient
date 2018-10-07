package jp.cordea.mackerelclient.model

import android.content.Context
import android.preference.PreferenceManager

class Preferences(context: Context) {

    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    var userId: Int
        get() {
            return pref.getInt(USER_ID_KEY, -1)
        }
        set(value) {
            pref.edit().putInt(USER_ID_KEY, value).apply()
        }

    fun clear() {
        pref.edit().remove(USER_ID_KEY).apply()
    }

    companion object {

        private const val USER_ID_KEY = "UserIdKey"
    }
}
