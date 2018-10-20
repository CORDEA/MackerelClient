package jp.cordea.mackerelclient.model

class UserDefinedMetrics(
    val id: Int,
    val label: String,
    metric0: String,
    metric1: String?
) {
    companion object {
        fun from(metrics: UserMetric) =
            UserDefinedMetrics(metrics.id, metrics.label!!, metrics.metric0, metrics.metric1)
    }

    val metrics = if (metric1 == null) listOf(metric0) else listOf(metric0, metric1)
}
