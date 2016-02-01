package jp.cordea.mackerelclient.api.response

/**
 * Created by CORDEA on 2016/01/16.
 */
class Metrics(val metrics: List<Metric>)

class Metric(val time: Long, val value: Float)