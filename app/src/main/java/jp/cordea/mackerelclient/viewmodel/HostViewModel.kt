package jp.cordea.mackerelclient.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.HostsResponse
import jp.cordea.mackerelclient.api.response.Tsdbs
import jp.cordea.mackerelclient.model.DisplayHostState
import jp.cordea.mackerelclient.repository.HostRepository
import javax.inject.Inject

typealias HostsWithTsdbs = Pair<HostsResponse, Tsdbs>

class HostViewModel : ViewModel() {
    @Inject
    lateinit var context: Context

    @Inject
    lateinit var repository: HostRepository

    private var hosts: HostsResponse? = null
    private var tsdbs: Tsdbs? = null

    private val serialDisposable = SerialDisposable()

    val adapterItems = PublishSubject.create<HostsWithTsdbs>()
    val isProgressLayoutVisible = PublishSubject.create<Boolean>()
    val isSwipeRefreshLayoutVisible = PublishSubject.create<Boolean>()
    val isErrorLayoutVisible = PublishSubject.create<Boolean>()
    val isRefreshing = PublishSubject.create<Boolean>()

    private val displayHostState: List<DisplayHostState>
        get() =
            repository.getDisplayHostStates(
                context.resources.getStringArray(R.array.setting_host_cell_arr)
            ).filter { it.isDisplay }

    fun clickedRetryButton() {
        isProgressLayoutVisible.onNext(true)
        isErrorLayoutVisible.onNext(false)
        refresh(true)
    }

    fun refresh(forceRefresh: Boolean) {
        isRefreshing.onNext(true)
        if (!forceRefresh && hosts != null && tsdbs != null) {
            Single.just(hosts!! to tsdbs!!)
        } else {
            getHosts(displayHostState)
                .flatMap { hosts ->
                    getLatestMetrics(hosts).map { hosts to it }
                }
        }
            .subscribe({
                adapterItems.onNext(it)
                isProgressLayoutVisible.onNext(false)
                isSwipeRefreshLayoutVisible.onNext(true)
                isRefreshing.onNext(false)
            }, {
                isErrorLayoutVisible.onNext(true)
                isProgressLayoutVisible.onNext(false)
                isSwipeRefreshLayoutVisible.onNext(false)
                isRefreshing.onNext(false)
            })
            .run(serialDisposable::set)
    }

    private fun getHosts(items: List<DisplayHostState>): Single<HostsResponse> =
        repository.getHosts(items)
            .doOnSuccess { hosts ->
                repository.deleteOldMetrics(hosts.hosts.map { it.id })
            }
            .doOnSuccess { hosts = it }

    private fun getLatestMetrics(hosts: HostsResponse): Single<Tsdbs> =
        repository.getLatestMetrics(
            hosts,
            listOf(loadavgMetricsKey, cpuMetricsKey, memoryMetricsKey)
        )
            .doOnSuccess { tsdbs = it }

    override fun onCleared() {
        super.onCleared()
        serialDisposable.dispose()
    }

    companion object {
        const val loadavgMetricsKey = "loadavg5"
        const val cpuMetricsKey = "cpu.user.percentage"
        const val memoryMetricsKey = "memory.used"
    }
}
