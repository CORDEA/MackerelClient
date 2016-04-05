package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Alert

/**
 * Created by Yoshihiro Tanaka on 16/01/13.
 */
class AlertAdapter(context: Context, val items: List<Alert>) : ArrayAdapter<Alert>(context, R.layout.list_item_alert) {

    override fun getItem(position: Int): Alert {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_alert, parent, false)

        val item = getItem(position)

        val detail: TextView = view.findViewById(R.id.detail) as TextView
        if (!item.type.isNullOrBlank() || !item.status.isNullOrBlank()) {
            if (item.type.isNullOrBlank()) {
                detail.text = item.status
            } else if (item.status.isNullOrBlank()) {
                detail.text = item.type
            } else {
                detail.text = item.type + " / " + item.status
            }
        }

        val name: TextView = view.findViewById(R.id.name) as TextView
        name.text = item.hostId

        return view
    }
}