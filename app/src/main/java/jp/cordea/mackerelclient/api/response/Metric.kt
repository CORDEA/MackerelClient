package jp.cordea.mackerelclient.api.response

class Metrics(val metrics: List<Metric>)

class Metric(val time: Long, val value: Float)
