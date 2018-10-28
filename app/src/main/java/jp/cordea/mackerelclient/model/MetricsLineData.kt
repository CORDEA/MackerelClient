package jp.cordea.mackerelclient.model

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import jp.cordea.mackerelclient.api.response.MetricsResponse
import jp.cordea.mackerelclient.utils.DateUtils

class MetricsLineDataSet(
    val id: Int,
    val label: String?,
    val data: LineData
) {
    companion object {
        val ERROR = MetricsLineDataSet(0, null, LineData())
    }
}

class MetricsLineData(
    val label: String,
    val x: List<MetricsLineX>,
    val y: List<MetricsLineY>
) {
    companion object {
        fun from(name: String, response: MetricsResponse) =
            MetricsLineData(
                name,
                response.metrics.map { MetricsLineX(it.time) },
                response.metrics.map { MetricsLineY(it.value) }
            )
    }

    fun toLineDataSet(color: Int): LineDataSet =
        LineDataSet(y.mapIndexed { index, y -> Entry(y.value, index) }, label).apply {
            lineWidth = 2f
            setDrawCircles(false)
            setDrawCircleHole(false)
            setDrawValues(false)
            this.color = color
        }
}

class MetricsLineY(
    val value: Float
)

class MetricsLineX(
    value: Long
) {
    val value = DateUtils.stringFromEpoch(value)
}

fun List<LineDataSet>.toLineData(xValues: List<String>): LineData =
    LineData(xValues, first()).also {
        if (size > 1) {
            it.addDataSet(this[1])
        }
    }
