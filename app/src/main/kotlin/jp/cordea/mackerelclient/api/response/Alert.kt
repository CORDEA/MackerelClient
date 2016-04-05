package jp.cordea.mackerelclient.api.response

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Yoshihiro Tanaka on 16/01/13.
 */
class Alerts(val alerts: List<Alert>)

class CloseAlert(val reason: String)

class Alert : Parcelable {

    var id: String? = null
    var status: String? = null
    var monitorId: String? = null
    var type: String? = null
    var hostId: String? =null
    var value: Float? = null
    var message: String? = null
    var reason: String? = null
    var openedAt: Long? = null
    var closedAt: Long? = null

    companion object {
        @JvmField
        public val CREATOR: Parcelable.Creator<Alert> = object : Parcelable.Creator<Alert> {
            override fun createFromParcel(p0: Parcel): Alert? {
                return Alert(p0)
            }

            override fun newArray(p0: Int): Array<out Alert>? {
                return Array(p0, {i -> Alert()})
            }

            private fun Alert(pin: Parcel): Alert {
                val alert = Alert()

                alert.id = pin.readString()
                alert.status = pin.readString()
                alert.monitorId = pin.readString()
                alert.type = pin.readString()
                alert.hostId = pin.readString()
                alert.value = pin.readString()?.toFloat()
                alert.message = pin.readString()
                alert.reason = pin.readString()
                alert.openedAt = pin.readLong()
                alert.closedAt = pin.readString()?.toLong()

                return alert
            }
        }
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(id)
        p0.writeString(status)
        p0.writeString(monitorId)
        p0.writeString(type)
        p0.writeString(hostId)
        p0.writeString(value?.toString())
        p0.writeString(message)
        p0.writeString(reason)
        p0.writeLong(openedAt!!)
        p0.writeString(closedAt?.toString())

    }

    override fun describeContents(): Int {
        return 0
    }
}
