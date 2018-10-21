package jp.cordea.mackerelclient

import android.content.Context

fun Long.toRelativeTime(context: Context, current: Long): String {
    val currentSec = current / 1000f
    var diff = currentSec - this
    if (diff <= 59) {
        return context.getString(R.string.relative_time_format_seconds, diff.toInt())
    }
    diff /= 60
    if (diff <= 59) {
        return context.getString(R.string.relative_time_format_minutes, diff.toInt())
    }
    diff /= 60
    if (diff <= 23) {
        return context.getString(R.string.relative_time_format_hours, diff.toInt())
    }
    diff /= 24
    if (diff <= 9) {
        return context.getString(R.string.relative_time_format_days, diff.toInt())
    }
    return context.getString(R.string.relative_time_format_others)
}
