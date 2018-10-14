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
class ServiceMetricsRepository @Inject constructor(
    private val remoteDataSource: ServiceMetricsRemoteDataSource,
    private val localDataSource: ServiceMetricsLocalDataSource
) {
    fun getMetrics(
        serviceName: String,
        metrics: UserDefinedMetrics,
        from: Long,
        to: Long
    ): Single<List<MetricsLineData>> =
        Observable.fromIterable(metrics.metrics)
            .flatMapSingle { name ->
                remoteDataSource.getMetrics(serviceName, name, from, to)
                    .map { MetricsLineData.from(name, it) }
            }
            .toList()
            .subscribeOn(Schedulers.io())

    fun getMetricsDefinition(serviceName: String): List<UserMetric> =
        localDataSource.getMetricsDefinition(serviceName)
}
