package jp.cordea.mackerelclient.repository

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jp.cordea.mackerelclient.model.MetricsLineData
import jp.cordea.mackerelclient.model.UserDefinedMetrics
import jp.cordea.mackerelclient.model.UserMetric
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetricsRepository @Inject constructor(
    private val remoteDataSource: MetricsRemoteDataSource,
    private val localDataSource: MetricsLocalDataSource
) {
    fun getMetrics(
        hostId: String,
        metrics: UserDefinedMetrics,
        from: Long,
        to: Long
    ): Single<List<MetricsLineData>> =
        Observable.fromIterable(metrics.metrics)
            .flatMapSingle { name ->
                remoteDataSource.getMetrics(hostId, name, from, to)
                    .map { MetricsLineData.from(name, it) }
            }
            .toList()
            .subscribeOn(Schedulers.io())

    fun getMetricsDefinition(hostId: String): List<UserMetric> =
        localDataSource.getMetricsDefinition(hostId)

    fun storeDefaultUserMetrics(hostId: String) =
        localDataSource.storeDefaultUserMetrics(hostId)
}
