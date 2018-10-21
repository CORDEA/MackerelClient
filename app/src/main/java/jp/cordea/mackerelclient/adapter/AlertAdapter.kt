package jp.cordea.mackerelclient.adapter

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import jp.cordea.mackerelclient.di.FragmentScope
import jp.cordea.mackerelclient.view.CriticalAlertListItem
import jp.cordea.mackerelclient.view.CriticalAlertListItemModel
import javax.inject.Inject
import javax.inject.Provider

@FragmentScope
class AlertAdapter @Inject constructor(
    val item: Provider<CriticalAlertListItem>
) : GroupAdapter<ViewHolder>() {
    fun update(items: List<CriticalAlertListItemModel>) {
        clear()
        addAll(items.map { item.get().update(it) })
    }
}
