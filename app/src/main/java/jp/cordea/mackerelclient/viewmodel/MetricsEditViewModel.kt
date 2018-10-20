package jp.cordea.mackerelclient.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.subjects.PublishSubject
import jp.cordea.mackerelclient.repository.MetricsEditRepository
import javax.inject.Inject

class MetricsEditViewModel : ViewModel() {
    @Inject
    lateinit var repository: MetricsEditRepository

    val labelText = PublishSubject.create<String>()
    val metricFirstText = PublishSubject.create<String>()
    val metricSecondText = PublishSubject.create<String>()

    fun start(id: Int) {
        val metric = repository.getMetric(id)
        labelText.onNext(metric.label ?: "")
        metricFirstText.onNext(metric.metric0)
        metricSecondText.onNext(metric.metric1 ?: "")
    }

    fun storeMetric(
        id: Int,
        parentId: String,
        type: String,
        label: String,
        metric0: String,
        metric1: String?
    ) = repository.storeMetric(
        id,
        parentId,
        type,
        label,
        metric0,
        metric1
    )
}
