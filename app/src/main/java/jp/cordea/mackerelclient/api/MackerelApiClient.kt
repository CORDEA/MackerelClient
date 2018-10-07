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

object MackerelApiClient {

    private const val BASE_URL = "https://mackerel.io"

    private val GSON = GsonBuilder()
        .registerTypeAdapter(Tsdbs::class.java, TsdbsDeserializer())
        .create()

    private val BUILDER = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GSON))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    private fun <T> getService(
        service: java.lang.Class<T>,
        context: Context,
        k: String? = null
    ): T {
        var key = k
        if (key == null) {
            val userId = Preferences(context).userId
            val realm = Realm.getDefaultInstance()
            key = realm.copyFromRealm(
                realm.where(UserKey::class.java).equalTo("id", userId).findFirst()!!
            ).key!!
            realm.close()
        }
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

        return BUILDER
            .client(httpClient)
            .build()
            .create(service)
    }

    fun getServices(context: Context): Single<Services> =
        getService(MackerelApi::class.java, context)
            .getService()
            .subscribeOn(Schedulers.io())

    fun getHosts(context: Context, status: List<String>): Single<Hosts> =
        getService(MackerelApi::class.java, context)
            .getAllHosts(status)
            .subscribeOn(Schedulers.io())

    fun getAlerts(context: Context): Single<Alerts> =
        getService(MackerelApi::class.java, context)
            .getAlerts()
            .subscribeOn(Schedulers.io())

    fun getMonitors(context: Context): Single<Monitors> =
        getService(MackerelApi::class.java, context)
            .getMonitors()
            .subscribeOn(Schedulers.io())

    fun getUsers(context: Context, key: String? = null): Single<Users> =
        getService(MackerelApi::class.java, context, key)
            .getUsers()
            .subscribeOn(Schedulers.io())

    fun getMetrics(
        context: Context,
        hostId: String,
        name: String,
        from: Long,
        to: Long
    ): Single<Metrics> =
        getService(MackerelApi::class.java, context)
            .getMetrics(hostId, name, from, to)
            .subscribeOn(Schedulers.io())

    fun getLatestMetrics(
        context: Context,
        hostId: List<String>,
        name: List<String>
    ): Single<Tsdbs> =
        getService(MackerelApi::class.java, context)
            .getLatestMetric(hostId, name)
            .subscribeOn(Schedulers.io())

    fun getServiceMetrics(
        context: Context,
        serviceName: String,
        name: String,
        from: Long,
        to: Long
    ): Single<Metrics> =
        getService(MackerelApi::class.java, context)
            .getServiceMetrics(serviceName, name, from, to)
            .subscribeOn(Schedulers.io())

    fun deleteUser(context: Context, userId: String): Call<User> =
        getService(MackerelApi::class.java, context).deleteUser(userId)

    fun closeAlert(context: Context, alertId: String, close: CloseAlert): Call<Alert> =
        getService(MackerelApi::class.java, context).postCloseAlert(alertId, close)

    fun retireHost(context: Context, hostId: String): Call<RetireHost> =
        getService(MackerelApi::class.java, context).postRetireHost(hostId)

    fun deleteMonitor(context: Context, monitorId: String): Call<Monitor> =
        getService(MackerelApi::class.java, context).deleteMonitor(monitorId)

    fun refreshMonitor(
        context: Context,
        monitorId: String,
        monitor: Monitor
    ): Call<RefreshMonitor> =
        getService(MackerelApi::class.java, context).putRefreshMonitor(monitorId, monitor)
}
