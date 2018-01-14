package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Alert

class AlertAdapter(context: Context, val items: List<Alert>) : ArrayAdapter<Alert>(context, R.layout.list_item_alert) {

    override fun getItem(position: Int): Alert {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

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

        viewHolder.apply {
            if (!item.type.isNullOrBlank() || !item.status.isNullOrBlank()) {
                if (item.type.isNullOrBlank()) {
                    detailTextView.text = item.status
                } else if (item.status.isNullOrBlank()) {
                    detailTextView.text = item.type
                } else {
                    detailTextView.text = item.type + " / " + item.status
                }
            }

            nameTextView.text = item.hostId
        }

        return view
    }

    class ViewHolder(view: View) {

        val nameTextView = view.findViewById(R.id.name) as TextView

        val detailTextView = view.findViewById(R.id.detail) as TextView

    }
}
