package jp.cordea.mackerelclient.viewmodel

import com.github.mikephil.charting.utils.ColorTemplate
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.cordea.mackerelclient.model.MetricsLineDataSet
import jp.cordea.mackerelclient.model.UserDefinedMetrics
import jp.cordea.mackerelclient.model.toLineData
import jp.cordea.mackerelclient.repository.ServiceMetricsRepository
import jp.cordea.mackerelclient.utils.DateUtils
import javax.inject.Inject

class ServiceMetricsViewModel @Inject constructor(
    private val repository: ServiceMetricsRepository
) {
    private lateinit var serviceName: String

    fun start(serviceName: String) {
        this.serviceName = serviceName
    }

    fun fetchMetrics(): Observable<MetricsLineDataSet> {
        val from = DateUtils.getEpochSec(1)
        val to = DateUtils.getEpochSec(0)
        return Observable.fromIterable(repository.getMetricsDefinition(serviceName))
            .map { UserDefinedMetrics.from(it) }
            .concatMapSingle { metrics ->
                repository.getMetrics(serviceName, metrics, from, to)
                    .flatMap { data ->
                        Observable.range(0, data.size)
                            .map { data[it].toLineDataSet(ColorTemplate.COLORFUL_COLORS[it]) }
                            .toList()
                            .map { list -> list.toLineData(data.first().x.map { it.value }) }
                            .map { MetricsLineDataSet(metrics.id, metrics.label, it) }
                    }
            }
            .observeOn(AndroidSchedulers.mainThread())
    }
}
