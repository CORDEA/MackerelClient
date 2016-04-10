package jp.cordea.mackerelclient.api.response

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by CORDEA on 2016/01/11.
 */
class Hosts(val hosts: List<Host>)

class RetireHost(val success: Boolean)

class Host : Parcelable {

    var createdAt: Long? = null
    var id: String? = null
    var name: String? = null
    var displayName: String? = null
    var status: String? = null
    var memo: String? = null
    var roles: Map<String, Array<String>> = mapOf()

    companion object {
        @JvmField
        public val CREATOR: Parcelable.Creator<Host> = object : Parcelable.Creator<Host> {
            override fun createFromParcel(p0: Parcel): Host? {
                return Host(p0)
            }

            override fun newArray(p0: Int): Array<out Host>? {
                return Array(p0, { i -> Host() })
            }

            private fun Host(pin: Parcel): Host {
                val host = Host()

                host.createdAt = pin.readLong()
                host.id = pin.readString()
                host.name = pin.readString()
                host.displayName = pin.readString()
                host.status = pin.readString()
                host.memo = pin.readString()

                val map: MutableMap<String, Array<String>> = hashMapOf()
                for (i in 0..pin.readInt() - 1) {
                    val key = pin.readString()
                    map.put(key, pin.createStringArray())
                }
                host.roles = map

                return host
            }
        }
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeLong(createdAt!!)
        p0.writeString(id!!)
        p0.writeString(name!!)
        p0.writeValue(displayName)
        p0.writeString(status!!)
        p0.writeString(memo!!)
        p0.writeInt(roles.size)
        for (v in roles) {
            p0.writeString(v.key)
            p0.writeStringArray(v.value)
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}
