package jp.cordea.mackerelclient

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.YAxisValueFormatter

class MemoryValueFormatter : YAxisValueFormatter {

    override fun getFormattedValue(value: Float, yAxis: YAxis?): String? =
        "%.1f".format(value / 1024.0f / 1024.0f / 1024.0f)
}
