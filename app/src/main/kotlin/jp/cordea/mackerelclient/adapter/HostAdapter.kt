package jp.cordea.mackerelclient.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.HostDetailActivity
import jp.cordea.mackerelclient.activity.MetricsActivity
import jp.cordea.mackerelclient.api.response.Host
import jp.cordea.mackerelclient.api.response.Tsdb
import jp.cordea.mackerelclient.utils.StatusUtils
import jp.cordea.mackerelclient.viewmodel.HostListItemViewModel
import kotterknife.bindView

/**
 * Created by Yoshihiro Tanaka on 16/01/12.
 */
class HostAdapter(
        val fragment: android.support.v4.app.Fragment,
        val items: List<Host>,
        private val metrics: Map<String, Map<String, Tsdb>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val context = fragment.context ?: return
        val item = items[position]
        (holder as? ViewHolder)?.apply {
            cardView.setOnClickListener {
                val intent = MetricsActivity.createIntent(context, item.id)
                fragment.startActivity(intent)
            }

            detailButton.setOnClickListener {
                val intent = HostDetailActivity.createIntent(context, item)
                fragment.startActivityForResult(intent, HostDetailActivity.RequestCode)
            }

            val metric = metrics[item.id]
            val viewModel = HostListItemViewModel(context, item, metric)

            if (item.displayName.isNullOrBlank()) {
                name.text = item.name
            } else {
                name.text = item.displayName
            }

            detail.text = item.memo
            role.text = viewModel.roleText

            health.setBackgroundColor(
                    ContextCompat.getColor(context, StatusUtils.stringToStatusColor(item.status))
            )

            loadavgTitleTextView.text = fragment.resources.getString(R.string.host_card_loadavg_title)
            cpuTitleTextView.text = fragment.resources.getString(R.string.host_card_cpu_title)
            memoryTitleTextView.text = fragment.resources.getString(R.string.host_card_memory_title)

            loadavgTextView.text = viewModel.loadavgText
            cpuTextView.text = viewModel.cpuText
            memoryTextView.text = viewModel.memoryText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        val view = LayoutInflater.from(fragment.context).inflate(R.layout.list_item_host, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val cardView: View by bindView(R.id.card_view)

        val name: TextView by bindView(R.id.name)

        val detail: TextView by bindView(R.id.detail)

        val role: TextView by bindView(R.id.role)

        val health: View by bindView(R.id.health)

        val loadavg: View by bindView(R.id.loadavg)

        val cpu: View by bindView(R.id.cpu)

        val memory: View by bindView(R.id.memory)

        val detailButton: Button by bindView(R.id.detail_button)

        val loadavgTextView: TextView = loadavg.findViewById(R.id.value) as TextView

        val loadavgTitleTextView: TextView = loadavg.findViewById(R.id.title) as TextView

        val cpuTextView: TextView = cpu.findViewById(R.id.value) as TextView

        val cpuTitleTextView: TextView = cpu.findViewById(R.id.title) as TextView

        val memoryTextView: TextView = memory.findViewById(R.id.value) as TextView

        val memoryTitleTextView: TextView = memory.findViewById(R.id.title) as TextView

    }
}
