package jp.cordea.mackerelclient.api.response

class MetricsResponse(val metrics: List<MetricResponse>)

class MetricResponse(val time: Long, val value: Float)
