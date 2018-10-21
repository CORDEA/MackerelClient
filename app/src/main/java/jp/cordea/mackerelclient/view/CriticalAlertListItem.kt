package jp.cordea.mackerelclient.view

import android.content.Context
import com.xwray.groupie.databinding.BindableItem
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.databinding.ListItemAlertBinding
import jp.cordea.mackerelclient.model.DisplayableAlert
import jp.cordea.mackerelclient.navigator.CriticalAlertNavigator
import jp.cordea.mackerelclient.toRelativeTime
import javax.inject.Inject

class CriticalAlertListItem @Inject constructor(
    private val navigator: CriticalAlertNavigator
) : BindableItem<ListItemAlertBinding>() {
    private lateinit var model: CriticalAlertListItemModel

    fun update(model: CriticalAlertListItemModel) = apply {
        this.model = model
    }

    override fun getLayout(): Int = R.layout.list_item_alert

    override fun bind(binding: ListItemAlertBinding, position: Int) {
        binding.model = model
        binding.root.setOnClickListener {
            navigator.navigateToDetail(model.alert)
        }
    }
}

class CriticalAlertListItemModel(
    val alert: DisplayableAlert
) {
    fun getName(context: Context) =
        "${alert.hostName} - ${alert.openedAt.toRelativeTime(context, System.currentTimeMillis())}"

    val availableState get() = alert.value != null
    val state get() = "${alert.value} ${alert.operator} ${alert.critical}"
    val type get() = alert.monitorName ?: alert.type
}
