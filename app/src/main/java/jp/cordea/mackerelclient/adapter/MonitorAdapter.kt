package jp.cordea.mackerelclient.adapter

import android.content.Context
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import jp.cordea.mackerelclient.api.response.MonitorDataResponse
import jp.cordea.mackerelclient.di.FragmentScope
import jp.cordea.mackerelclient.view.MonitorListItem
import jp.cordea.mackerelclient.view.MonitorListItemModel
import jp.cordea.mackerelclient.view.MonitorSectionListItem
import jp.cordea.mackerelclient.view.MonitorSectionListItemModel
import javax.inject.Inject
import javax.inject.Provider

@FragmentScope
class MonitorAdapter @Inject constructor(
    private val context: Context,
    private val sectionListItem: Provider<MonitorSectionListItem>,
    private val listItem: Provider<MonitorListItem>
) : GroupAdapter<ViewHolder>() {
    fun update(items: Map<String, List<MonitorDataResponse>>) {
        clear()
        items.forEach { entry ->
            add(sectionListItem.get().update(MonitorSectionListItemModel(entry.key)))
            addAll(entry.value.map {
                listItem.get().update(MonitorListItemModel.from(context, it))
            })
        }
    }
}
