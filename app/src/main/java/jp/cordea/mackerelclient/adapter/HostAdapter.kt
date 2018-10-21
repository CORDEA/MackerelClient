package jp.cordea.mackerelclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.HostDetailActivity
import jp.cordea.mackerelclient.activity.MetricsActivity
import jp.cordea.mackerelclient.api.response.HostDataResponse
import jp.cordea.mackerelclient.api.response.Tsdb
import jp.cordea.mackerelclient.databinding.ListItemHostBinding
import jp.cordea.mackerelclient.utils.StatusUtils
import jp.cordea.mackerelclient.viewmodel.HostListItemViewModel

class HostAdapter(
    val fragment: Fragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = emptyList<HostDataResponse>()
    private var metrics = emptyMap<String, Map<String, Tsdb>>()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val context = fragment.context ?: return
        val item = items[position]
        (holder as? ViewHolder)?.binding?.run {
            cardView.setOnClickListener {
                val intent = MetricsActivity.createIntent(context, item.id)
                fragment.startActivity(intent)
            }

            detailButton.setOnClickListener {
                val intent = HostDetailActivity.createIntent(context, item)
                fragment.startActivityForResult(intent, HostDetailActivity.REQUEST_CODE)
            }

            val metric = metrics[item.id]
            val viewModel = HostListItemViewModel(context, item, metric)

            nameTextView.text = if (item.displayName.isNullOrBlank()) {
                item.name
            } else {
                item.displayName
            }

            detailTextView.text = item.memo
            roleTextView.text = viewModel.roleText

            healthView.setBackgroundColor(
                ContextCompat.getColor(context, StatusUtils.stringToStatusColor(item.status))
            )

            loadavg.run {
                titleTextView.text = fragment.resources.getString(R.string.host_card_loadavg_title)
                valueTextView.text = viewModel.loadavgText
            }
            cpu.run {
                titleTextView.text = fragment.resources.getString(R.string.host_card_cpu_title)
                valueTextView.text = viewModel.cpuText
            }
            memory.run {
                titleTextView.text = fragment.resources.getString(R.string.host_card_memory_title)
                valueTextView.text = viewModel.memoryText
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(fragment.context)
            .inflate(R.layout.list_item_host, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    fun update(items: List<HostDataResponse>, metrics: Map<String, Map<String, Tsdb>>) {
        this.items = items
        this.metrics = metrics
        notifyDataSetChanged()
    }

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ListItemHostBinding = ListItemHostBinding.bind(view)
    }
}
