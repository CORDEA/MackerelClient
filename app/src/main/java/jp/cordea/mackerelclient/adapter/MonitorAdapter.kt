package jp.cordea.mackerelclient.adapter

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.MonitorDetailActivity
import jp.cordea.mackerelclient.api.response.Monitor
import jp.cordea.mackerelclient.databinding.ListItemMonitorBinding
import jp.cordea.mackerelclient.databinding.ListItemMonitorSectionBinding

class MonitorAdapter(
    val fragment: Fragment,
    val items: List<Pair<String, Monitor?>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val context = fragment.context ?: return
        (holder as? ViewHolder)?.binding?.run {
            items[position].second?.let { item ->
                root.setOnClickListener {
                    val intent = MonitorDetailActivity.createIntent(context, item)
                    fragment.startActivityForResult(intent, MonitorDetailActivity.REQUEST_CODE)
                }
                if (!item.name.isNullOrBlank()) {
                    nameTextView.text = item.name
                }
                idTextView.text = item.id
                return
            }
        }
        (holder as? SectionViewHolder)?.binding?.run {
            titleTextView.text = items[position].first
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view = LayoutInflater.from(fragment.context)
                .inflate(R.layout.list_item_monitor_section, parent, false)
            return SectionViewHolder(view)
        }
        val view = LayoutInflater.from(fragment.context)
            .inflate(R.layout.list_item_monitor, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int = if (items[position].second == null) 1 else 0

    override fun getItemCount(): Int = items.size

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ListItemMonitorBinding = ListItemMonitorBinding.bind(view)
    }

    private class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ListItemMonitorSectionBinding = ListItemMonitorSectionBinding.bind(view)
    }
}
