package jp.cordea.mackerelclient.model

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import jp.cordea.mackerelclient.api.response.MetricsResponse
import jp.cordea.mackerelclient.utils.DateUtils

sealed class MetricsLineDataSet(
    val id: Int
) {
    class Success(
        id: Int,
        val title: String?,
        private val data: List<MetricsLineData>
    ) : MetricsLineDataSet(id) {
        private val xValues: List<String>
            get() = data.first().x.map { it.value }

        private fun getLineData(index: Int) =
            data[index].toLineDataSet(ColorTemplate.COLORFUL_COLORS[index])

        fun toLineData(): LineData =
            LineData(xValues, getLineData(0)).also {
                if (data.size > 1) {
                    it.addDataSet(getLineData(1))
                }
            }
    }

    class Failure(
        id: Int
    ) : MetricsLineDataSet(id)
}

class MetricsLineData(
    val label: String,
    val x: List<MetricsLineX>,
    private val y: List<MetricsLineY>
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
