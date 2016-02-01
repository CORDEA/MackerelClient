package jp.cordea.mackerelclient

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.YAxisValueFormatter

/**
 * Created by Yoshihiro Tanaka on 16/01/19.
 */
class MemoryValueFormatter : YAxisValueFormatter {
    override fun getFormattedValue(value: Float, yAxis: YAxis?): String? {
        return "%.1f".format(value / 1024.0f / 1024.0f / 1024.0f)
    }
}