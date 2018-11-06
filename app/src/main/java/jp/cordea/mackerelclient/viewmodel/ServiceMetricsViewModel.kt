package jp.cordea.mackerelclient.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.cordea.mackerelclient.model.MetricsLineDataSet
import jp.cordea.mackerelclient.model.UserDefinedMetrics
import jp.cordea.mackerelclient.repository.ServiceMetricsRepository
import jp.cordea.mackerelclient.utils.DateUtils
import javax.inject.Inject

class ServiceMetricsViewModel : ViewModel() {
    @Inject
    lateinit var repository: ServiceMetricsRepository

    private lateinit var serviceName: String

    private var lineDataSet = mutableSetOf<MetricsLineDataSet>()

    private val metricsDefinition get() = repository.getMetricsDefinition(serviceName)

    val isExistsMetricsDefinition get() = metricsDefinition.isNotEmpty()

    fun start(serviceName: String) {
        this.serviceName = serviceName
    }

    fun fetchMetrics(forceRefresh: Boolean): Observable<MetricsLineDataSet> {
        val from = DateUtils.getEpochSec(1)
        val to = DateUtils.getEpochSec(0)
        return if (!forceRefresh && lineDataSet.isNotEmpty()) {
            Observable.fromIterable(lineDataSet)
        } else {
            lineDataSet.clear()
            Observable.fromIterable(metricsDefinition)
                .map { UserDefinedMetrics.from(it) }
                .concatMapSingle { metrics ->
                    repository.getMetrics(serviceName, metrics, from, to)
                        .map<MetricsLineDataSet> {
                            MetricsLineDataSet.Success(
                                metrics.id,
                                metrics.label,
                                it
                            )
                        }
                        .onErrorReturnItem(MetricsLineDataSet.Failure(metrics.id))
                }
                .doOnNext { lineDataSet.add(it) }
        }
            .observeOn(AndroidSchedulers.mainThread())
    }
}
