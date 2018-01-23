package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Service

class ServiceAdapter(
        context: Context,
        val items: List<Service>
) : ArrayAdapter<Service>(
        context,
        R.layout.list_item_service
) {

    override fun getItem(position: Int): Service =
            items[position]

    override fun getCount(): Int =
            items.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_service, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val item = getItem(position)
        viewHolder.apply {
            nameTextView.text = item.name
            roleTextView.text =
                    item.roles.size.let {
                        if (it <= 1) {
                            context.resources.getString(R.string.format_role).format(it)
                        } else {
                            if (it > 99) {
                                context.resources.getString(R.string.format_roles_ex)
                            } else {
                                context.resources.getString(R.string.format_roles).format(it)
                            }
                        }
                    }
            detailTextView.text = item.memo
        }

        return view
    }

    class ViewHolder(view: View) {

        val nameTextView = view.findViewById(R.id.name) as TextView

        val detailTextView = view.findViewById(R.id.detail) as TextView

        val roleTextView = view.findViewById(R.id.role) as TextView

    }
}
