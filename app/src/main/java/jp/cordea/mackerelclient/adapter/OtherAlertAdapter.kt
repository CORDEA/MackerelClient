package jp.cordea.mackerelclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.api.response.Alert
import jp.cordea.mackerelclient.databinding.ListItemOtherAlartBinding
import jp.cordea.mackerelclient.di.FragmentScope
import javax.inject.Inject

@FragmentScope
class OtherAlertAdapter @Inject constructor(
    context: Context
) : ArrayAdapter<Alert>(
    context,
    R.layout.list_item_other_alart
) {
    private var items = listOf<Alert>()

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

    override fun getItem(position: Int): Alert = items[position]

    override fun getCount(): Int = items.size

    fun update(items: List<Alert>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) {
        val binding: ListItemOtherAlartBinding = ListItemOtherAlartBinding.bind(view)
    }
}
