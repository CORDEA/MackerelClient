package jp.cordea.mackerelclient.adapter

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.MonitorDetailActivity
import jp.cordea.mackerelclient.api.response.Monitor
import kotterknife.bindView

class MonitorAdapter(
        val fragment: Fragment,
        val items: List<Pair<String, Monitor?>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val context = fragment.context ?: return
        (holder as? ViewHolder)?.run {
            items[position].second?.let { item ->
                cell.setOnClickListener {
                    val intent = MonitorDetailActivity.createIntent(context, item)
                    fragment.startActivityForResult(intent, MonitorDetailActivity.RequestCode)
                }
                if (!item.name.isNullOrBlank()) {
                    name.text = item.name
                }
                id.text = item.id
                return
            }
        }
        (holder as? SectionViewHolder)?.run {
            title.text = items[position].first
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view = LayoutInflater.from(fragment.context)
                    .inflate(R.layout.list_item_monitor_section, parent, false)
            return SectionViewHolder(view)
        }
        val view = LayoutInflater.from(fragment.context)
                .inflate(R.layout.list_item_monitor, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].second == null) 1 else 0
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val cell: View by bindView(R.id.cell)
        val name: TextView by bindView(R.id.name)
        val id: TextView by bindView(R.id.id)
    }

    private class SectionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView by bindView(R.id.title)
    }
}
