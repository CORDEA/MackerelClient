package jp.cordea.mackerelclient.api.response

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Yoshihiro Tanaka on 16/01/15.
 */
class Monitors(val monitors: List<Monitor>)

class RefreshMonitor(val id: String)

class Monitor : Parcelable {

    var id: String? = null
    var type: String? = null
    var name: String? = null
    var service: String? = null
    var duration: Int? = null
    var metric: String? = null
    var operator: String? = null
    var warning: Float? = null
    var critical: Float? = null
    var notificationInterval: Int? = null
    var url: String? = null
    var scopes: Array<String> = arrayOf()
    var excludeScopes: Array<String> = arrayOf()

    companion object {
        public val CREATOR: Parcelable.Creator<Monitor> = object : Parcelable.Creator<Monitor> {
            override fun createFromParcel(p0: Parcel): Monitor? {
                return Monitor(p0)
            }

            override fun newArray(p0: Int): Array<out Monitor>? {
                return Array(p0, {i -> Monitor()})
            }

            private fun Monitor(pin: Parcel): Monitor {
                val monitor = Monitor()

                monitor.id = pin.readString()
                monitor.type = pin.readString()
                monitor.name = pin.readString()
                monitor.service = pin.readString()
                monitor.duration = pin.readString()?.toInt()
                monitor.metric = pin.readString()
                monitor.operator = pin.readString()
                monitor.warning = pin.readString()?.toFloat()
                monitor.critical = pin.readString()?.toFloat()
                monitor.notificationInterval = pin.readString()?.toInt()
                monitor.url = pin.readString()
                monitor.scopes = pin.createStringArray()
                monitor.excludeScopes = pin.createStringArray()

                return monitor
            }
        }
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(id!!)
        p0.writeString(type!!)
        p0.writeString(name)
        p0.writeString(service)
        p0.writeString(duration?.toString())
        p0.writeString(metric)
        p0.writeString(operator)
        p0.writeString(warning?.toString())
        p0.writeString(critical?.toString())
        p0.writeString(notificationInterval?.toString())
        p0.writeString(url)
        p0.writeStringArray(scopes)
        p0.writeStringArray(excludeScopes)
    }

    override fun describeContents(): Int {
        return 0
    }

}