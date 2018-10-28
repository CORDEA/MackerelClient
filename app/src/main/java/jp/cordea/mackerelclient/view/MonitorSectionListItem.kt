package jp.cordea.mackerelclient.view

import com.xwray.groupie.databinding.BindableItem
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.databinding.ListItemMonitorSectionBinding
import javax.inject.Inject

class MonitorSectionListItem @Inject constructor() : BindableItem<ListItemMonitorSectionBinding>() {
    private lateinit var model: MonitorSectionListItemModel

    fun update(model: MonitorSectionListItemModel) = apply { this.model = model }

    override fun getLayout(): Int = R.layout.list_item_monitor_section

    override fun bind(binding: ListItemMonitorSectionBinding, position: Int) {
        binding.model = model
    }
}

class MonitorSectionListItemModel(
    val label: String
)
