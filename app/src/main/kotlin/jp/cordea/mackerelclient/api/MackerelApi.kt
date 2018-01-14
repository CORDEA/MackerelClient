package jp.cordea.mackerelclient.api

import jp.cordea.mackerelclient.api.request.EmptyBody
import jp.cordea.mackerelclient.api.response.*
import retrofit2.Call
import retrofit2.http.*
import rx.Observable

/**
 * Created by CORDEA on 2016/01/11.
 */
interface MackerelApi {

    @GET("/api/v0/services")
    fun getService(): Observable<Services>

    @GET("/api/v0/services/{serviceName}/roles")
    fun getRoles(@Path("serviceName") serviceName: String): Observable<Roles>

    @GET("/api/v0/hosts/{hostId}")
    fun getHost(@Path("hostId") hostId: String): Observable<Host>

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
            @Query("to") to: Long): Observable<Metrics>

    @GET("/api/v0/hosts")
    fun getAllHosts(
            @Query("status") status: List<String>,
            @Query("service") service: String? = null,
            @Query("role") role: List<String>? = null,
            @Query("name") name: String? = null
    ): Observable<Hosts>

    @GET("/api/v0/monitors")
    fun getMonitors(): Observable<Monitors>

    @GET("/api/v0/tsdb/latest")
    fun getLatestMetric(
            @Query("hostId") hostId: List<String>,
            @Query("name") name: List<String>
    ): Observable<Tsdbs>

    @GET("/api/v0/services/{serviceName}/metrics")
    fun getServiceMetrics(
            @Path("serviceName") serviceName: String,
            @Query("name") name: String,
            @Query("from") from: Long,
            @Query("to") to: Long
    ): Observable<Metrics>

    @GET("/api/v0/alerts")
    fun getAlerts(): Observable<Alerts>

    @GET("/api/v0/users")
    fun getUsers(): Observable<Users>

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
