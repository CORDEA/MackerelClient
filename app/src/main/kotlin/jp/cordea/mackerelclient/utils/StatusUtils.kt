package jp.cordea.mackerelclient.utils

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

        public fun stringToStatusColor(status: String): Int {
            when (status) {
                "working" -> return R.color.statusWorking
                "standby" -> return R.color.statusStandby
                "maintenance" -> return R.color.statusMaintenance
                "poweroff" -> return R.color.statusPoweroff
                else -> return R.color.statusPoweroff
            }
        }
    }
}