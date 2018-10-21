package jp.cordea.mackerelclient.viewmodel

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.HostDataResponse
import jp.cordea.mackerelclient.api.response.Tsdb

class HostListItemViewModel(
    private val context: Context,
    private val item: HostDataResponse,
    private val metric: Map<String, Tsdb>?
) {

    val roleText: String
        get() = item.roles.size.let {
            if (it <= 1) {
                context.resources.getString(R.string.format_role).format(it)
            } else {
                if (it > 99) {
                    context.resources.getString(R.string.format_roles_ex)
                } else {
                    context.resources.getString(R.string.format_roles).format(it)
                }
            }
        }

    val loadavgText: String
        get() {
            metric?.get(HostViewModel.loadavgMetricsKey)?.let { met ->
                return "%.2f".format(met.metricValue)
            }
            return ""
        }

    val cpuText: SpannableStringBuilder
        get() {
            val sp = SpannableStringBuilder()
            metric?.get(HostViewModel.cpuMetricsKey)?.let { met ->
                sp.append("%.1f %%".format(met.metricValue))
                sp.setSpan(
                    TextAppearanceSpan(context, R.style.HostMetricUnit),
                    sp.length - 1, sp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            return sp
        }

    val memoryText: SpannableStringBuilder
        get() {
            val sp = SpannableStringBuilder()
            metric?.get(HostViewModel.memoryMetricsKey)?.let { met ->
                var unit = "MB"
                var mem = (met.metricValue ?: 0.0f) / 1024.0f / 1024.0f
                if (mem > 999) {
                    unit = "GB"
                    mem /= 1024.0f
                }
                if (mem > 999) {
                    sp.append("999+ %s".format(unit))
                } else {
                    sp.append("%.0f %s".format(mem, unit))
                }
                sp.setSpan(
                    TextAppearanceSpan(context, R.style.HostMetricUnit),
                    sp.length - 2, sp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            return sp
        }
}
