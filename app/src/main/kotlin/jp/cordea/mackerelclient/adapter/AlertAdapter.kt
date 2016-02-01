package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pawegio.kandroid.find
import com.pawegio.kandroid.inflateLayout
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
        val view = convertView ?: context.inflateLayout(R.layout.list_item_alert, parent)

        val item = getItem(position)

        val detail: TextView = view.find(R.id.detail)
        if (!item.type.isNullOrBlank() || !item.status.isNullOrBlank()) {
            if (item.type.isNullOrBlank()) {
                detail.text = item.status
            } else if (item.status.isNullOrBlank()) {
                detail.text = item.type
            } else {
                detail.text = item.type + " / " + item.status
            }
        }

        val name: TextView = view.find(R.id.name)
        name.text = item.hostId

        return view
    }
}