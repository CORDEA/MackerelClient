package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pawegio.kandroid.find
import com.pawegio.kandroid.inflateLayout
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Service

/**
 * Created by CORDEA on 2016/01/11.
 */
class ServiceAdapter(context: Context, val items: List<Service>) : ArrayAdapter<Service>(context, R.layout.list_item_service) {

    override fun getItem(position: Int): Service? {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view = convertView ?: context.inflateLayout(R.layout.list_item_service, parent)

        val item = getItem(position)
        item ?: return convertView
        val name: TextView = view.find(R.id.name)
        name.text = item.name
        val role: TextView = view.find(R.id.role)
        role.text =
                item.roles.size.let {
                    if (it <= 1) context.resources.getString(R.string.format_role).format(it)
                        else
                            if (it > 99) context.resources.getString(R.string.format_roles_ex)
                            else context.resources.getString(R.string.format_roles).format(it)
                }
        val detail: TextView = view.find(R.id.detail)
        detail.text = item.memo

        return view
    }
}