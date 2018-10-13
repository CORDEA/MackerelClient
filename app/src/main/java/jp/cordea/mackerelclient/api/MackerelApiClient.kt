package jp.cordea.mackerelclient.api

import android.content.Context
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import jp.cordea.mackerelclient.BuildConfig
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.api.response.Alerts
import jp.cordea.mackerelclient.api.response.CloseAlert
import jp.cordea.mackerelclient.api.response.Hosts
import jp.cordea.mackerelclient.api.response.Metrics
import jp.cordea.mackerelclient.api.response.Monitor
import jp.cordea.mackerelclient.api.response.Monitors
import jp.cordea.mackerelclient.api.response.RefreshMonitor
import jp.cordea.mackerelclient.api.response.RetireHost
import jp.cordea.mackerelclient.api.response.Services
import jp.cordea.mackerelclient.api.response.Tsdbs
import jp.cordea.mackerelclient.api.response.TsdbsDeserializer
import jp.cordea.mackerelclient.api.response.User
import jp.cordea.mackerelclient.api.response.Users
import jp.cordea.mackerelclient.model.Preferences
import jp.cordea.mackerelclient.model.UserKey
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MackerelApiClient @Inject constructor(
    private val context: Context
) {
    private val gson = GsonBuilder()
        .registerTypeAdapter(Tsdbs::class.java, TsdbsDeserializer())
        .create()

    private val builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    private val userKey: String by lazy {
        val userId = Preferences(context).userId
        Realm.getDefaultInstance().use {
            it.copyFromRealm(
                it.where(UserKey::class.java).equalTo("id", userId).findFirst()!!
            ).key!!
        }
    }

    private fun <T> getService(
        service: java.lang.Class<T>,
        k: String? = null
    ): T {
        val key = k ?: userKey
        var httpClientBuilder =
            OkHttpClient.Builder()
                .addInterceptor {
                    it.proceed(
                        it.request()
                            .newBuilder()
                            .addHeader("X-Api-Key", key)
                            .addHeader("Content-Type", "application/json")
                            .build()
                    )
                }

        if (BuildConfig.DEBUG) {
            httpClientBuilder = httpClientBuilder
                .addInterceptor(
                    okhttp3.logging.HttpLoggingInterceptor()
                        .setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BASIC)
                )
        }

        val httpClient = httpClientBuilder.build()

        return builder
            .client(httpClient)
            .build()
            .create(service)
    }

    fun getServices(): Single<Services> =
        getService(MackerelApi::class.java)
            .getService()
            .subscribeOn(Schedulers.io())

    fun getHosts(status: List<String>): Single<Hosts> =
        getService(MackerelApi::class.java)
            .getAllHosts(status)
            .subscribeOn(Schedulers.io())

    fun getAlerts(): Single<Alerts> =
        getService(MackerelApi::class.java)
            .getAlerts()
            .subscribeOn(Schedulers.io())

    fun getMonitors(): Single<Monitors> =
        getService(MackerelApi::class.java)
            .getMonitors()
            .subscribeOn(Schedulers.io())

    fun getUsers(key: String? = null): Single<Users> =
        getService(MackerelApi::class.java, key)
            .getUsers()
            .subscribeOn(Schedulers.io())

    fun getMetrics(
        hostId: String,
        name: String,
        from: Long,
        to: Long
    ): Single<Metrics> =
        getService(MackerelApi::class.java)
            .getMetrics(hostId, name, from, to)
            .subscribeOn(Schedulers.io())

    fun getLatestMetrics(
        hostId: List<String>,
        name: List<String>
    ): Single<Tsdbs> =
        getService(MackerelApi::class.java)
            .getLatestMetric(hostId, name)
            .subscribeOn(Schedulers.io())

    fun getServiceMetrics(
        serviceName: String,
        name: String,
        from: Long,
        to: Long
    ): Single<Metrics> =
        getService(MackerelApi::class.java)
            .getServiceMetrics(serviceName, name, from, to)
            .subscribeOn(Schedulers.io())

    fun deleteUser(userId: String): Call<User> =
        getService(MackerelApi::class.java).deleteUser(userId)

    fun closeAlert(alertId: String, close: CloseAlert): Call<Alert> =
        getService(MackerelApi::class.java).postCloseAlert(alertId, close)

    fun retireHost(hostId: String): Call<RetireHost> =
        getService(MackerelApi::class.java).postRetireHost(hostId)

    fun deleteMonitor(monitorId: String): Call<Monitor> =
        getService(MackerelApi::class.java).deleteMonitor(monitorId)

    fun refreshMonitor(monitorId: String, monitor: Monitor): Call<RefreshMonitor> =
        getService(MackerelApi::class.java).putRefreshMonitor(monitorId, monitor)

    companion object {
        private const val BASE_URL = "https://mackerel.io"
    }
}
