package jp.cordea.mackerelclient.api

import android.content.Context
import com.google.gson.GsonBuilder
import io.realm.Realm
import jp.cordea.mackerelclient.BuildConfig
import jp.cordea.mackerelclient.api.response.*
import jp.cordea.mackerelclient.model.Preferences
import jp.cordea.mackerelclient.model.UserKey
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import rx.schedulers.Schedulers

object MackerelApiClient {

    private const val BASE_URL = "https://mackerel.io"

    private val GSON = GsonBuilder()
            .registerTypeAdapter(Tsdbs::class.java, TsdbsDeserializer())
            .create()

    private val BUILDER = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GSON))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())

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
                    realm.where(UserKey::class.java).equalTo("id", userId).findFirst()).key
            realm.close()
        }
        var httpClientBuilder =
                OkHttpClient.Builder()
                        .addInterceptor {
                            it.proceed(it.request()
                                    .newBuilder()
                                    .addHeader("X-Api-Key", key)
                                    .addHeader("Content-Type", "application/json")
                                    .build())
                        }

        if (BuildConfig.DEBUG) {
            httpClientBuilder = httpClientBuilder
                    .addInterceptor(okhttp3.logging.HttpLoggingInterceptor()
                            .setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BASIC))
        }

        val httpClient = httpClientBuilder.build()

        return BUILDER
                .client(httpClient)
                .build()
                .create(service)
    }

    fun getServices(context: Context): Observable<Services> {
        return getService(MackerelApi::class.java, context)
                .getService()
                .subscribeOn(Schedulers.newThread())
    }

    fun getHosts(context: Context, status: List<String>): Observable<Hosts> {
        return getService(MackerelApi::class.java, context)
                .getAllHosts(status)
                .subscribeOn(Schedulers.newThread())
    }

    fun getAlerts(context: Context): Observable<Alerts> {
        return getService(MackerelApi::class.java, context)
                .getAlerts()
                .subscribeOn(Schedulers.newThread())
    }

    fun getMonitors(context: Context): Observable<Monitors> {
        return getService(MackerelApi::class.java, context)
                .getMonitors()
                .subscribeOn(Schedulers.newThread())
    }

    fun getUsers(context: Context, key: String? = null): Observable<Users> {
        return getService(MackerelApi::class.java, context, key)
                .getUsers()
                .subscribeOn(Schedulers.newThread())
    }

    fun getMetrics(context: Context, hostId: String, name: String, from: Long, to: Long): Observable<Metrics> {
        return getService(MackerelApi::class.java, context)
                .getMetrics(hostId, name, from, to)
                .subscribeOn(Schedulers.newThread())
    }

    fun getLatestMetrics(context: Context, hostId: List<String>, name: List<String>): Observable<Tsdbs> {
        return getService(MackerelApi::class.java, context)
                .getLatestMetric(hostId, name)
                .subscribeOn(Schedulers.newThread())
    }

    fun getServiceMetrics(context: Context, serviceName: String, name: String, from: Long, to: Long): Observable<Metrics> {
        return getService(MackerelApi::class.java, context)
                .getServiceMetrics(serviceName, name, from, to)
                .subscribeOn(Schedulers.newThread())
    }

    fun deleteUser(context: Context, userId: String): Call<User> {
        return getService(MackerelApi::class.java, context)
                .deleteUser(userId)
    }

    fun closeAlert(context: Context, alertId: String, close: CloseAlert): Call<Alert> {
        return getService(MackerelApi::class.java, context)
                .postCloseAlert(alertId, close)
    }

    fun retireHost(context: Context, hostId: String): Call<RetireHost> {
        return getService(MackerelApi::class.java, context)
                .postRetireHost(hostId)
    }

    fun deleteMonitor(context: Context, monitorId: String): Call<Monitor> {
        return getService(MackerelApi::class.java, context)
                .deleteMonitor(monitorId)
    }

    fun refreshMonitor(context: Context, monitorId: String, monitor: Monitor): Call<RefreshMonitor> {
        return getService(MackerelApi::class.java, context)
                .putRefreshMonitor(monitorId, monitor)
    }
}
