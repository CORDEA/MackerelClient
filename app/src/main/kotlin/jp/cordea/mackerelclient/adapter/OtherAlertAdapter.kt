package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.databinding.ListItemOtherAlartBinding

class OtherAlertAdapter(
        context: Context,
        val items: List<Alert>
) : ArrayAdapter<Alert>(
        context,
        R.layout.list_item_other_alart
) {

    override fun getItem(position: Int): Alert =
            items[position]

    override fun getCount(): Int =
            items.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_other_alart, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val item = getItem(position)

        viewHolder.binding.run {
            detailTextView.text = "${item.type} / ${item.status}"
            nameTextView.text = item.hostId
            statusView.char = item.status.first()
        }

        return view
    }

    class ViewHolder(view: View) {

        val binding: ListItemOtherAlartBinding = ListItemOtherAlartBinding.bind(view)
    }
}
