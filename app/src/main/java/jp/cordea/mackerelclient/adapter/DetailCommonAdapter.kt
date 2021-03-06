package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.databinding.ListItemDetailCommonCardBinding

class DetailCommonAdapter(
    val context: Context,
    val items: List<List<Pair<String, Int>>>,
    private val sections: List<String>? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ViewHolder)?.binding?.let {
            if (items[position].isNotEmpty()) {
                sections?.let { sections ->
                    val section = LayoutInflater.from(context)
                        .inflate(R.layout.list_item_detail_common_section, it.container, false)
                    val name: TextView = section.findViewById(R.id.title) as TextView
                    it.container.addView(section)
                    name.text = sections[position]
                }

                var divider: View? = null
                for (item in items[position]) {
                    if (item.first.isBlank()) {
                        continue
                    }
                    val layout = LayoutInflater.from(context)
                        .inflate(R.layout.list_item_detail_common_content, it.container, false)
                    val title: TextView = layout.findViewById(R.id.title) as TextView
                    val detail: TextView = layout.findViewById(R.id.detail) as TextView
                    divider = layout.findViewById(R.id.divider)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.list_item_detail_common_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.filter { it.isNotEmpty() }.size

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ListItemDetailCommonCardBinding = ListItemDetailCommonCardBinding.bind(view)
    }
}
