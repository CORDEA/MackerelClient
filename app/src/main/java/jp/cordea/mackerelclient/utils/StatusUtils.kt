package jp.cordea.mackerelclient.utils

import jp.cordea.mackerelclient.R

object StatusUtils {

    fun stringToRequestName(name: String): String =
            if (name == "Power off") {
                "poweroff"
            } else {
                name.toLowerCase()
            }

    fun requestNameToString(status: String): String =
            when (status) {
                "working" -> "Working"
                "standby" -> "Standby"
                "maintenance" -> "Maintenance"
                "poweroff" -> "Power off"
                else -> status
            }

    fun stringToStatusColor(status: String): Int =
            when (status) {
                "working" -> R.color.statusWorking
                "standby" -> R.color.statusStandby
                "maintenance" -> R.color.statusMaintenance
                "poweroff" -> R.color.statusPoweroff
                else -> R.color.statusPoweroff
            }
}
