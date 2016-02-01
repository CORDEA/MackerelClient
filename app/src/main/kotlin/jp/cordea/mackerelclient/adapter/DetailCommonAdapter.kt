package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.bindView
import com.pawegio.kandroid.find
import com.pawegio.kandroid.inflateLayout
import jp.cordea.mackerelclient.R

/**
 * Created by Yoshihiro Tanaka on 16/01/21.
 */
class DetailCommonAdapter(val context: Context, val items: List<List<Pair<String, Int>>>, val sections: List<String>? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as? ViewHolder)?.let {
            if (items[position].size > 0) {
                sections?.let { sections ->
                    val section = context.inflateLayout(R.layout.list_item_detail_common_section, it.container, false)
                    val name: TextView = section.find(R.id.title)
                    it.container.addView(section)
                    name.text = sections[position]
                }

                var divider: View? = null
                for (item in items[position]) {
                    if (item.first.isNullOrBlank()) {
                        continue
                    }
                    val layout = context.inflateLayout(R.layout.list_item_detail_common_content, it.container, false)
                    val title: TextView = layout.find(R.id.title)
                    val detail: TextView = layout.find(R.id.detail)
                    divider = layout.find(R.id.divider)
                    title.text = item.first
                    detail.text = context.resources.getString(item.second)
                    it.container.addView(layout)
                }
                divider?.let {
                    it.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var view = context.inflateLayout(R.layout.list_item_detail_common_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.filter { it.size > 0 }.size
    }

    private class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout by bindView(R.id.container)
    }
}