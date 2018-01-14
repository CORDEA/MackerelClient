package jp.cordea.mackerelclient.viewmodel

import android.content.Context
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.ogaclejapan.rx.binding.RxEvent
import io.realm.Realm
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.api.MackerelApiClient
import jp.cordea.mackerelclient.api.response.Metrics
import jp.cordea.mackerelclient.model.MetricsApiRequestParameter
import jp.cordea.mackerelclient.model.UserMetric
import jp.cordea.mackerelclient.utils.DateUtils
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.onErrorReturnNull
import java.util.concurrent.TimeUnit

class MetricsViewModel(val context: Context) {

    private val apiResponses: MutableList<MetricsApiRequestParameter> = arrayListOf()

    private var nofMetrics: MutableList<Int> = arrayListOf()

    val onChartDataAlive: RxEvent<Pair<Int, LineData?>> = RxEvent.create()

    var subscription: Subscription? = null

    fun initUserMetrics(parentId: String) {
        val realm = Realm.getDefaultInstance()
        val c = realm.where(UserMetric::class.java)
                .equalTo("type", MetricsType.HOST.name)
                .equalTo("parentId", parentId).findAll().size
        if (c > 0) {
            realm.close()
            return
        }

        val maxId = (realm.where(UserMetric::class.java).max("id") ?: 0).toInt()
        val metrics = arrayListOf<UserMetric>().apply {
            add(UserMetric().apply {
                type = MetricsType.HOST.name
                id = maxId + 1
                this.parentId = parentId
                label = "loadavg5"
                metric0 = "loadavg5"
            })
            add(UserMetric().apply {
                type = MetricsType.HOST.name
                id = maxId + 2
                this.parentId = parentId
                label = "cpu percentage"
                metric0 = "cpu.system.percentage"
                metric1 = "cpu.user.percentage"
            })
            add(UserMetric().apply {
                type = MetricsType.HOST.name
                id = maxId + 3
                this.parentId = parentId
                label = "memory"
                metric0 = "memory.used"
                metric1 = "memory.free"
            })
        }

        realm.beginTransaction()
        realm.copyToRealm(metrics)
        realm.commitTransaction()
        realm.close()
    }

    private fun hostMetrics(
            hostId: String,
            param: MetricsApiRequestParameter
    ): Observable<Metrics> =
            MackerelApiClient.getMetrics(
                    context,
                    hostId,
                    param.metricsName,
                    DateUtils.getEpochSec(1),
                    DateUtils.getEpochSec(0)
            )

    private fun serviceMetrics(
            serviceName: String,
            param: MetricsApiRequestParameter
    ): Observable<Metrics> =
            MackerelApiClient.getServiceMetrics(
                    context,
                    serviceName,
                    param.metricsName,
                    DateUtils.getEpochSec(1),
                    DateUtils.getEpochSec(0)
            )

    fun requestMetricsApi(metrics: List<UserMetric>, id: String, idType: MetricsType) {
        val requests: MutableList<MetricsApiRequestParameter> = arrayListOf()
        val nofMetrics: MutableList<Int> = arrayListOf()
        for (metric in metrics) {
            var nof = 1
            requests.add(MetricsApiRequestParameter(metric.id, metric.metric0!!))
            metric.metric1?.let {
                requests.add(MetricsApiRequestParameter(metric.id, it))
                ++nof
            }
            nofMetrics.add(nof)
        }
        this.nofMetrics = nofMetrics
        runMetricsApiWithDelay(id, idType, requests)
    }

    private fun runMetricsApiWithDelay(
            id: String,
            idType: MetricsType,
            metricsApiRequestParameters: List<MetricsApiRequestParameter>,
            idx: Int = 0
    ) {
        if (metricsApiRequestParameters.size <= idx) {
            return
        }

        val param = metricsApiRequestParameters[idx]

        val metricsObservable: Observable<Metrics>
        if (idType == MetricsType.SERVICE) {
            metricsObservable = serviceMetrics(id, param)
        } else {
            metricsObservable = hostMetrics(id, param)
        }

        subscription = metricsObservable
                .onErrorReturnNull()
                .delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    assert(nofMetrics.size > 0)

                    apiResponses.add(param.copy(response = it))
                    if (nofMetrics.first() == apiResponses.size) {
                        setData(apiResponses)
                        nofMetrics.removeAt(0)
                        apiResponses.clear()
                    }

                    runMetricsApiWithDelay(id, idType, metricsApiRequestParameters, idx + 1)
                }, {
                    it.printStackTrace()
                    apiResponses.add(param.copy(response = null))
                    if (nofMetrics.first() == apiResponses.size) {
                        setData(apiResponses)
                        nofMetrics.removeAt(0)
                        apiResponses.clear()
                    }

                    runMetricsApiWithDelay(id, idType, metricsApiRequestParameters, idx + 1)
                })
    }

    private fun setData(metricsApiRequestParameters: List<MetricsApiRequestParameter>) {
        val color = ColorTemplate.COLORFUL_COLORS
        var data: LineData? = null

        if (metricsApiRequestParameters.filter { it.response != null }.isEmpty()) {
            onChartDataAlive.post(Pair(metricsApiRequestParameters.first().id, null))
        } else {
            for ((j, param) in metricsApiRequestParameters.withIndex()) {
                val vals = param.response?.metrics ?: continue
                val metricName = param.metricsName

                val xValues: MutableList<String> = arrayListOf()
                val yValues: MutableList<Entry> = arrayListOf()
                for ((i, v) in vals.withIndex()) {
                    xValues.add(i, DateUtils.stringFromEpoch(v.time))
                    yValues.add(i, Entry(v.value, i))
                }

                val dataSet = LineDataSet(yValues.toList(), metricName).apply {
                    lineWidth = 2f
                    setDrawCircles(false)
                    setDrawCircleHole(false)
                    setDrawValues(false)
                    this.color = color[j]
                }

                if (data == null) {
                    data = LineData(xValues, dataSet)
                } else {
                    data.addDataSet(dataSet)
                }
            }

            onChartDataAlive.post(Pair(metricsApiRequestParameters.first().id, data))
        }
    }
}
