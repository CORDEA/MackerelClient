package jp.cordea.mackerelclient.adapter

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import jp.cordea.mackerelclient.di.FragmentScope
import jp.cordea.mackerelclient.view.AlertListItem
import jp.cordea.mackerelclient.view.AlertListItemModel
import javax.inject.Inject
import javax.inject.Provider

@FragmentScope
class AlertAdapter @Inject constructor(
    val item: Provider<AlertListItem>
) : GroupAdapter<ViewHolder>() {
    fun update(items: List<AlertListItemModel>) {
        clear()
        addAll(items.map { item.get().update(it) })
    }
}
