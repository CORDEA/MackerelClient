package jp.cordea.mackerelclient.adapter

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import jp.cordea.mackerelclient.di.FragmentScope
import jp.cordea.mackerelclient.view.OtherAlertListItem
import jp.cordea.mackerelclient.view.OtherAlertListItemModel
import javax.inject.Inject
import javax.inject.Provider

@FragmentScope
class OtherAlertAdapter @Inject constructor(
    val item: Provider<OtherAlertListItem>
) : GroupAdapter<ViewHolder>() {
    fun update(items: List<OtherAlertListItemModel>) {
        clear()
        addAll(items.map { item.get().update(it) })
    }
}
