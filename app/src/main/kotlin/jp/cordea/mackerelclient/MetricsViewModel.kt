package jp.cordea.mackerelclient

import android.content.Context
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.ogaclejapan.rx.binding.RxEvent
import io.realm.Realm
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

/**
 * Created by Yoshihiro Tanaka on 16/01/19.
 */
class MetricsViewModel(val context: Context) {

    public final val onChartDataAlive: RxEvent<Pair<Int, LineData?>> = RxEvent.create()

    private val apiResponses: MutableList<MetricsApiRequestParameter> = arrayListOf()
    private var nofMetrics: MutableList<Int> = arrayListOf()

    public var subscription : Subscription? = null

    public fun initializeUserMetrics(id: String) {
        val realm = Realm.getInstance(context)
        val c = realm.where(UserMetric::class.java)
                .equalTo("type", MetricsType.HOST.name)
                .equalTo("parentId", id).findAll().size
        if (c > 0) {
            realm.close()
            return
        }

        var maxId = (realm.allObjects(UserMetric::class.java).max("id") ?: 0).toInt()
        val metrics: MutableList<UserMetric> = arrayListOf()
        var metric = UserMetric()
        metric.type = MetricsType.HOST.name
        metric.id = maxId + 1
        metric.parentId = id
        metric.label = "loadavg5"
        metric.metric0 = "loadavg5"
        metrics.add(metric)
        metric = UserMetric()
        metric.type = MetricsType.HOST.name
        metric.id = maxId + 2
        metric.parentId = id
        metric.label = "cpu percentage"
        metric.metric0 = "cpu.system.percentage"
        metric.metric1 = "cpu.user.percentage"
        metrics.add(metric)
        metric = UserMetric()
        metric.type = MetricsType.HOST.name
        metric.id = maxId + 3
        metric.parentId = id
        metric.label = "memory"
        metric.metric0 = "memory.used"
        metric.metric1 = "memory.free"
        metrics.add(metric)

        realm.beginTransaction()
        realm.copyToRealm(metrics)
        realm.commitTransaction()
        realm.close()
    }

    private fun hostMetrics(hostId: String, param: MetricsApiRequestParameter): Observable<Metrics> {
        return MackerelApiClient
                    .getMetrics(context, hostId, param.metricsName, DateUtils.getEpochSec(1), DateUtils.getEpochSec(0))
    }

    private fun serviceMetrics(serviceName: String, param: MetricsApiRequestParameter): Observable<Metrics> {
        return MackerelApiClient
                    .getServiceMetrics(context, serviceName, param.metricsName, DateUtils.getEpochSec(1), DateUtils.getEpochSec(0))
    }

    public fun requestMetricsApi(metrics: List<UserMetric>, id: String, idType: MetricsType) {
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

    public fun runMetricsApiWithDelay(id: String, idType: MetricsType, metricsApiRequestParameters: List<MetricsApiRequestParameter>, idx: Int = 0) {
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

        if (metricsApiRequestParameters.filter { it.response != null }.size == 0) {
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

                val dataSet = LineDataSet(yValues.toList(), metricName)
                dataSet.lineWidth = 2f
                dataSet.setDrawCircles(false)
                dataSet.setDrawCircleHole(false)
                dataSet.setDrawValues(false)
                dataSet.color = color[j]

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