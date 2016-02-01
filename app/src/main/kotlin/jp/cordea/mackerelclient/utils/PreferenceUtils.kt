package jp.cordea.mackerelclient.utils

import android.content.Context
import com.pawegio.kandroid.defaultSharedPreferences

/**
 * Created by Yoshihiro Tanaka on 16/01/22.
 */
class PreferenceUtils {
    companion object {
        private val userIdKey = "UserIdKey"

        public fun readUserId(context: Context): Int {
            val pref = context.defaultSharedPreferences
            return pref.getInt(userIdKey, -1)
        }

        public fun writeUserId(context: Context, id: Int) {
            val pref = context.defaultSharedPreferences
            pref.edit().putInt(userIdKey, id).apply()
        }

        public fun removeUserId(context: Context) {
            val pref = context.defaultSharedPreferences
            pref.edit().remove(userIdKey).apply()
        }
    }
}