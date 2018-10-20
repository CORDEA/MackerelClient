package jp.cordea.mackerelclient.viewmodel

import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.utils.ColorTemplate
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.cordea.mackerelclient.model.MetricsLineDataSet
import jp.cordea.mackerelclient.model.UserDefinedMetrics
import jp.cordea.mackerelclient.model.toLineData
import jp.cordea.mackerelclient.repository.MetricsRepository
import jp.cordea.mackerelclient.utils.DateUtils
import javax.inject.Inject

class MetricsViewModel : ViewModel() {
    @Inject
    lateinit var repository: MetricsRepository

    private lateinit var hostId: String

    private var lineDataSet = mutableSetOf<MetricsLineDataSet>()

    private val metricsDefinition get() = repository.getMetricsDefinition(hostId)

    val isExistsMetricsDefinition get() = metricsDefinition.isNotEmpty()

    fun start(hostId: String) {
        this.hostId = hostId
    }

    fun setDefaultUserMetrics(hostId: String) {
        repository.storeDefaultUserMetrics(hostId)
    }

    fun fetchMetrics(forceRefresh: Boolean): Observable<MetricsLineDataSet> {
        val from = DateUtils.getEpochSec(1)
        val to = DateUtils.getEpochSec(0)
        return if (!forceRefresh && lineDataSet.isNotEmpty()) {
            Observable.fromIterable(lineDataSet)
        } else {
            Observable.fromIterable(metricsDefinition)
                .map { UserDefinedMetrics.from(it) }
                .concatMapSingle { metrics ->
                    repository.getMetrics(hostId, metrics, from, to)
                        .flatMap { data ->
                            Observable.range(0, data.size)
                                .map { data[it].toLineDataSet(ColorTemplate.COLORFUL_COLORS[it]) }
                                .toList()
                                .map { list -> list.toLineData(data.first().x.map { it.value }) }
                                .map { MetricsLineDataSet(metrics.id, metrics.label, it) }
                        }
                }
                .onErrorReturnItem(MetricsLineDataSet.ERROR)
                .doOnNext { lineDataSet.add(it) }
        }
            .observeOn(AndroidSchedulers.mainThread())
    }
}
