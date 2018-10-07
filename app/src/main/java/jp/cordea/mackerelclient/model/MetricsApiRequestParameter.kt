package jp.cordea.mackerelclient.model

import com.github.mikephil.charting.data.LineData
import jp.cordea.mackerelclient.api.response.Metrics

data class MetricsApiRequestParameter(val id: Int, val metricsName: String, val response: Metrics? = null)

class MetricsParameter(val id: Int, val data: LineData?, val label: String, val isError: Boolean = false)
