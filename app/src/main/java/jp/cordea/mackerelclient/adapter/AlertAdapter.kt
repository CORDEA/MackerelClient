package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.databinding.ListItemAlertBinding
import jp.cordea.mackerelclient.di.FragmentScope
import javax.inject.Inject

@FragmentScope
class AlertAdapter @Inject constructor(
    context: Context
) : ArrayAdapter<Alert>(context, R.layout.list_item_alert) {
    private var items = listOf<Alert>()

    override fun getItem(position: Int): Alert = items[position]

    override fun getCount(): Int = items.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_alert, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val item = getItem(position)

        viewHolder.binding.run {
            if (!item.type.isBlank() || !item.status.isBlank()) {
                detailTextView.text = when {
                    item.type.isBlank() -> item.status
                    item.status.isBlank() -> item.type
                    else -> "${item.type} / ${item.status}"
                }
            }

            nameTextView.text = item.hostId
        }

        return view
    }

    fun update(items: List<Alert>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) {
        val binding: ListItemAlertBinding = ListItemAlertBinding.bind(view)
    }
}
