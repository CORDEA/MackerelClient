package jp.cordea.mackerelclient.model

import android.os.Parcelable
import jp.cordea.mackerelclient.api.response.HostDataResponse
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class DisplayableHost(
    val id: String,
    private val rawName: String,
    private val displayName: String?,
    val status: String,
    val memo: String,
    val numberOfRoles: Int,
    val createdAt: Long
) : Parcelable {
    companion object {
        fun from(host: HostDataResponse) =
            DisplayableHost(
                host.id,
                host.name,
                host.displayName,
                host.status,
                host.memo,
                host.roles.size,
                host.createdAt
            )
    }

    @IgnoredOnParcel
    val name: String = if (displayName.isNullOrBlank()) rawName else displayName!!
}
