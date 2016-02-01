package jp.cordea.mackerelclient.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.bindView
import com.pawegio.kandroid.inflateLayout
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.MonitorDetailActivity
import jp.cordea.mackerelclient.api.response.Monitor

/**
 * Created by Yoshihiro Tanaka on 16/01/15.
 */
class MonitorAdapter(val fragment: android.support.v4.app.Fragment, val items: List<Pair<String, Monitor?>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as? ViewHolder)?.let {
            items[position].second?.let { item ->
                it.cell.setOnClickListener {
                    val intent = Intent(fragment.context, MonitorDetailActivity::class.java)
                    intent.putExtra(MonitorDetailActivity.MonitorKey, item)
                    fragment.startActivityForResult(intent, MonitorDetailActivity.RequestCode)
                }
                if (!item.name.isNullOrBlank()) {
                    it.name.text = item.name
                }
                it.id.text = item.id
                return
            }
            it.title.text = items[position].first
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var view = fragment.context.inflateLayout(R.layout.list_item_monitor, parent, false)
        if (viewType == 1) {
            view = fragment.context.inflateLayout(R.layout.list_item_monitor_section, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].second == null) 1 else 0
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val cell: View by bindView(R.id.cell)

        val title: TextView by bindView(R.id.title)
        val name: TextView by bindView(R.id.name)
        val id: TextView by bindView(R.id.id)
    }
}