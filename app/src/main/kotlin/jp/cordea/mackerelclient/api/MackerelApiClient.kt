package jp.cordea.mackerelclient.api

import android.content.Context
import com.google.gson.GsonBuilder
import io.realm.Realm
import jp.cordea.mackerelclient.BuildConfig
import jp.cordea.mackerelclient.api.response.*
import jp.cordea.mackerelclient.model.UserKey
import jp.cordea.mackerelclient.utils.PreferenceUtils
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by CORDEA on 2016/01/11.
 */
class MackerelApiClient {
    companion object {
        private val baseUrl = "https://mackerel.io"

        private val gson = GsonBuilder()
                .registerTypeAdapter(Tsdbs::class.java, TsdbsDeserializer())
                .create()

        private val builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())

        private fun <T> getService(service: java.lang.Class<T>, context: Context, k: String? = null): T {
            var key = k
            if (key == null) {
                val userId = PreferenceUtils.readUserId(context)
                val realm = Realm.getDefaultInstance()
                key = realm.copyFromRealm(realm.where(UserKey::class.java).equalTo("id", userId).findFirst()).key
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

            return builder
                    .client(httpClient)
                    .build()
                    .create(service)
        }

        public fun getServices(context: Context): Observable<Services> {
            return getService(MackerelApi::class.java, context)
                    .getService()
                    .subscribeOn(Schedulers.newThread())
        }

        public fun getHosts(context: Context, status: List<String>): Observable<Hosts> {
            return getService(MackerelApi::class.java, context)
                    .getAllHosts(status)
                    .subscribeOn(Schedulers.newThread())
        }

        public fun getAlerts(context: Context): Observable<Alerts> {
            return getService(MackerelApi::class.java, context)
                    .getAlerts()
                    .subscribeOn(Schedulers.newThread())
        }

        public fun getMonitors(context: Context): Observable<Monitors> {
            return getService(MackerelApi::class.java, context)
                    .getMonitors()
                    .subscribeOn(Schedulers.newThread())
        }

        public fun getUsers(context: Context, key: String? = null): Observable<Users> {
            return getService(MackerelApi::class.java, context, key)
                    .getUsers()
                    .subscribeOn(Schedulers.newThread())
        }

        public fun getMetrics(context: Context, hostId: String, name: String, from: Long, to: Long): Observable<Metrics> {
            return getService(MackerelApi::class.java, context)
                    .getMetrics(hostId, name, from, to)
                    .subscribeOn(Schedulers.newThread())
        }

        public fun getLatestMetrics(context: Context, hostId: List<String>, name: List<String>): Observable<Tsdbs> {
            return getService(MackerelApi::class.java, context)
                    .getLatestMetric(hostId, name)
                    .subscribeOn(Schedulers.newThread())
        }

        public fun getServiceMetrics(context: Context, serviceName: String, name: String, from: Long, to: Long): Observable<Metrics> {
            return getService(MackerelApi::class.java, context)
                    .getServiceMetrics(serviceName, name, from, to)
                    .subscribeOn(Schedulers.newThread())
        }

        public fun deleteUser(context: Context, userId: String): Call<User> {
            return getService(MackerelApi::class.java, context)
                    .deleteUser(userId)
        }

        public fun closeAlert(context: Context, alertId: String, close: CloseAlert): Call<Alert> {
            return getService(MackerelApi::class.java, context)
                    .postCloseAlert(alertId, close)
        }

        public fun retireHost(context: Context, hostId: String): Call<RetireHost> {
            return getService(MackerelApi::class.java, context)
                    .postRetireHost(hostId)
        }

        public fun deleteMonitor(context: Context, monitorId: String): Call<Monitor> {
            return getService(MackerelApi::class.java, context)
                    .deleteMonitor(monitorId)
        }

        public fun refreshMonitor(context: Context, monitorId: String, monitor: Monitor): Call<RefreshMonitor> {
            return getService(MackerelApi::class.java, context)
                    .putRefreshMonitor(monitorId, monitor)
        }
    }
}
