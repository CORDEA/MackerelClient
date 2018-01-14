package jp.cordea.mackerelclient.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {
    fun getEpochSec(beforeHour: Long): Long {
        val now = Date().time / 1000
        val bef = TimeUnit.SECONDS.convert(beforeHour, TimeUnit.HOURS)
        return now - bef
    }

    fun stringFromEpoch(epoch: Long): String {
        val date = Date(epoch * 1000)
        val format = SimpleDateFormat("HH:mm:ss")
        format.timeZone = TimeZone.getDefault()
        return format.format(date)
    }

    fun stringDateFromEpoch(epoch: Long): String {
        val date = Date(epoch * 1000)
        val format = DateFormat.getDateTimeInstance()
        format.timeZone = TimeZone.getDefault()
        return format.format(date)
    }
}
