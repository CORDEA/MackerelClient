package jp.cordea.mackerelclient.view

import android.content.Context
import com.xwray.groupie.databinding.BindableItem
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.databinding.ListItemOtherAlertBinding
import jp.cordea.mackerelclient.model.DisplayableAlert
import jp.cordea.mackerelclient.navigator.OtherAlertNavigator
import jp.cordea.mackerelclient.toRelativeTime
import javax.inject.Inject

class OtherAlertListItem @Inject constructor(
    private val navigator: OtherAlertNavigator
) : BindableItem<ListItemOtherAlertBinding>() {
    private lateinit var model: OtherAlertListItemModel

    fun update(model: OtherAlertListItemModel) = apply {
        this.model = model
    }

    override fun getLayout(): Int = R.layout.list_item_other_alert

    override fun bind(binding: ListItemOtherAlertBinding, position: Int) {
        binding.model = model
        binding.root.setOnClickListener {
            navigator.navigateToDetail(model.alert)
        }
    }
}

class OtherAlertListItemModel(
    val alert: DisplayableAlert
) {
    fun getName(context: Context) =
        "${alert.hostName} - ${alert.openedAt.toRelativeTime(context)}"

    val availableState get() = alert.value != null
    val state get() = "${alert.value} ${alert.operator} ${alert.warning}"
    val type get() = alert.monitorName ?: alert.type
}
