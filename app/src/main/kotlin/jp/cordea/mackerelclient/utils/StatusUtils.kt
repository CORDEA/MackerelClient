package jp.cordea.mackerelclient.utils

import android.content.Context
import android.support.v4.content.ContextCompat
import jp.cordea.mackerelclient.R

/**
 * Created by CORDEA on 2016/01/31.
 */
class StatusUtils {
    companion object {
        public fun stringToRequestName(name: String): String {
            if (name.equals("Power off")) {
                return "poweroff"
            }
            return name.toLowerCase()
        }

        public fun requestNameToString(status: String): String {
            when (status) {
                "working" -> return "Working"
                "standby" -> return "Standby"
                "maintenance" -> return "Maintenance"
                "poweroff" -> return "Power off"
                else -> return status
            }
        }

        public fun stringToStatusColor(context: Context, status: String): Int {
            when (status) {
                "working" -> return ContextCompat.getColor(context, R.color.statusWorking)
                "standby" -> return ContextCompat.getColor(context, R.color.statusStandby)
                "maintenance" -> return ContextCompat.getColor(context, R.color.statusMaintenance)
                "poweroff" -> return ContextCompat.getColor(context, R.color.statusPoweroff)
                else -> return ContextCompat.getColor(context, R.color.statusPoweroff)
            }
        }
    }
}