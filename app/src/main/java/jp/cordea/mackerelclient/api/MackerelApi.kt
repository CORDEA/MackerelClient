package jp.cordea.mackerelclient.api

import io.reactivex.Single
import jp.cordea.mackerelclient.api.request.EmptyBody
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.api.response.Alerts
import jp.cordea.mackerelclient.api.response.CloseAlert
import jp.cordea.mackerelclient.api.response.Host
import jp.cordea.mackerelclient.api.response.Hosts
import jp.cordea.mackerelclient.api.response.MetricsResponse
import jp.cordea.mackerelclient.api.response.Monitor
import jp.cordea.mackerelclient.api.response.Monitors
import jp.cordea.mackerelclient.api.response.RefreshMonitor
import jp.cordea.mackerelclient.api.response.RetireHost
import jp.cordea.mackerelclient.api.response.Roles
import jp.cordea.mackerelclient.api.response.Services
import jp.cordea.mackerelclient.api.response.Tsdbs
import jp.cordea.mackerelclient.api.response.User
import jp.cordea.mackerelclient.api.response.Users
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MackerelApi {

    @GET("/api/v0/services")
    fun getService(): Single<Services>

    @GET("/api/v0/services/{serviceName}/roles")
    fun getRoles(@Path("serviceName") serviceName: String): Single<Roles>

    @GET("/api/v0/hosts/{hostId}")
    fun getHost(@Path("hostId") hostId: String): Single<Host>

    @POST("/api/v0/hosts/{hostId}/retire")
    fun postRetireHost(
        @Path("hostId") hostId: String,
        @Body emptyBody: EmptyBody = EmptyBody()
    ): Call<RetireHost>

    @GET("/api/v0/hosts/{hostId}/metrics")
    fun getMetrics(
        @Path("hostId") hostId: String,
        @Query("name") name: String,
        @Query("from") from: Long,
        @Query("to") to: Long
    ): Single<MetricsResponse>

    @GET("/api/v0/hosts")
    fun getAllHosts(
        @Query("status") status: List<String>,
        @Query("service") service: String? = null,
        @Query("role") role: List<String>? = null,
        @Query("name") name: String? = null
    ): Single<Hosts>

    @GET("/api/v0/monitors")
    fun getMonitors(): Single<Monitors>

    @GET("/api/v0/tsdb/latest")
    fun getLatestMetric(
        @Query("hostId") hostId: List<String>,
        @Query("name") name: List<String>
    ): Single<Tsdbs>

    @GET("/api/v0/services/{serviceName}/metrics")
    fun getServiceMetrics(
        @Path("serviceName") serviceName: String,
        @Query("name") name: String,
        @Query("from") from: Long,
        @Query("to") to: Long
    ): Single<MetricsResponse>

    @GET("/api/v0/alerts")
    fun getAlerts(): Single<Alerts>

    @GET("/api/v0/users")
    fun getUsers(): Single<Users>

    @POST("/api/v0/alerts/{alertId}/close")
    fun postCloseAlert(@Path("alertId") alertId: String, @Body close: CloseAlert): Call<Alert>

    @DELETE("/api/v0/users/{userId}")
    fun deleteUser(@Path("userId") userId: String): Call<User>

    @DELETE("/api/v0/monitors/{monitorId}")
    fun deleteMonitor(@Path("monitorId") monitorId: String): Call<Monitor>

    @PUT("/api/v0/monitors/{monitorId}")
    fun putRefreshMonitor(
        @Path("monitorId") monitorId: String,
        @Body monitor: Monitor
    ): Call<RefreshMonitor>
}
