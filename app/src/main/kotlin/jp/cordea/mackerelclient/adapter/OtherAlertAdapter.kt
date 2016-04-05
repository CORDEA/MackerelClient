package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.view.CharCircleView

/**
 * Created by Yoshihiro Tanaka on 16/01/19.
 */
class OtherAlertAdapter(context: Context, val items: List<Alert>) : ArrayAdapter<Alert>(context, R.layout.list_item_other_alart) {

    override fun getItem(position: Int): Alert {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_other_alart, parent, false)

        val item = getItem(position)

        val detail: TextView = view.findViewById(R.id.detail) as TextView
        detail.text = item.type + " / " + item.status
        val name: TextView = view.findViewById(R.id.name) as TextView
        name.text = item.hostId
        val status: CharCircleView = view.findViewById(R.id.status) as CharCircleView
        status.setChar(item.status!!.first())

        return view
    }
}