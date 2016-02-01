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
        val view = convertView ?: context.inflateLayout(R.layout.list_item_other_alart, parent)

        val item = getItem(position)

        val detail: TextView = view.find(R.id.detail)
        detail.text = item.type + " / " + item.status
        val name: TextView = view.find(R.id.name)
        name.text = item.hostId
        val status: CharCircleView = view.find(R.id.status)
        status.setChar(item.status!!.first())

        return view
    }
}