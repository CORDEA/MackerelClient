package jp.cordea.mackerelclient.repository

import jp.cordea.mackerelclient.model.UserMetric
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetricsEditRepository @Inject constructor(
    private val localDataSource: MetricsEditLocalDataSource
) {
    fun getMetric(id: Int): UserMetric = localDataSource.getMetric(id)

    fun storeMetric(
        id: Int,
        parentId: String,
        type: String,
        label: String,
        metric0: String,
        metric1: String
    ) = localDataSource.storeMetric(
        id,
        parentId,
        type,
        label,
        metric0,
        metric1
    )
}
