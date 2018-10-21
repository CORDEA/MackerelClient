package jp.cordea.mackerelclient.view

import android.content.Context
import com.xwray.groupie.databinding.BindableItem
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.databinding.ListItemAlertBinding
import jp.cordea.mackerelclient.model.DisplayableAlert
import jp.cordea.mackerelclient.toRelativeTime
import javax.inject.Inject

class AlertListItem @Inject constructor() : BindableItem<ListItemAlertBinding>() {
    private lateinit var model: AlertListItemModel

    fun update(model: AlertListItemModel) = apply {
        this.model = model
    }

    override fun getLayout(): Int = R.layout.list_item_alert

    override fun bind(binding: ListItemAlertBinding, position: Int) {
        binding.model = model
        binding.root.setOnClickListener {
            //            val intent = AlertDetailActivity
//                .createIntent(context, adapter.getItem(i))
//            parentFragment.startActivityForResult(intent, OtherAlertFragment.REQUEST_CODE)
        }
    }
}

class AlertListItemModel(
    val alert: DisplayableAlert
) {
    fun getName(context: Context) =
        "${alert.hostName} - ${alert.openedAt.toRelativeTime(context)}"

    val availableState get() = alert.value != null
    val state get() = "${alert.value} ${alert.operator} ${alert.critical}"
    val type get() = alert.monitorName ?: alert.type
}
