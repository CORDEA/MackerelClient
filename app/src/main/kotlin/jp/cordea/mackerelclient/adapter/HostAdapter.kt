package jp.cordea.mackerelclient.adapter

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import butterknife.bindView
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.HostDetailActivity
import jp.cordea.mackerelclient.activity.MetricsActivity
import jp.cordea.mackerelclient.api.response.Host
import jp.cordea.mackerelclient.api.response.Tsdb
import jp.cordea.mackerelclient.utils.StatusUtils

/**
 * Created by Yoshihiro Tanaka on 16/01/12.
 */
class HostAdapter(val fragment: android.support.v4.app.Fragment, val items: List<Host>, val metrics: Map<String, Map<String, Tsdb>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val item = items[position]
        (holder as? ViewHolder)?.let {
            it.cardView.setOnClickListener {
                val intent = Intent(fragment.context, MetricsActivity::class.java)
                intent.putExtra(MetricsActivity.HostIdKey, item.id)
                fragment.startActivity(intent)
            }

            it.detailButton.setOnClickListener {
                val intent = Intent(fragment.context, HostDetailActivity::class.java)
                intent.putExtra(HostDetailActivity.HostKey, item)
                fragment.startActivityForResult(intent, HostDetailActivity.RequestCode)
            }

            val metric = metrics[item.id]

            var name = item.name
            if (item.displayName != null) {
                name = item.displayName
            }

            it.name.text = name
            it.hostId.text = item.id
            it.detail.text = item.memo
            it.role.text =
                    item.roles.size.let {
                        if (it <= 1) fragment.resources.getString(R.string.format_role).format(it)
                        else
                            if (it > 99) fragment.resources.getString(R.string.format_roles_ex)
                            else fragment.resources.getString(R.string.format_roles).format(it)
                    }

            it.health.setBackgroundColor(
                    ContextCompat.getColor(fragment.context, StatusUtils.stringToStatusColor(item.status!!)))

            val loadavg: TextView = it.loadavg.findViewById(R.id.value) as TextView
            val loadavgTitle: TextView = it.loadavg.findViewById(R.id.title) as TextView
            val cpu: TextView = it.cpu.findViewById(R.id.value) as TextView
            val cpuTitle: TextView = it.cpu.findViewById(R.id.title) as TextView
            val memory: TextView = it.memory.findViewById(R.id.value) as TextView
            val memoryTitle: TextView = it.memory.findViewById(R.id.title) as TextView

            loadavgTitle.text = fragment.resources.getString(R.string.host_card_loadavg_title)
            cpuTitle.text = fragment.resources.getString(R.string.host_card_cpu_title)
            memoryTitle.text = fragment.resources.getString(R.string.host_card_memory_title)

            metric ?: return
            metric["loadavg5"]?.let {
                loadavg.text = "%.2f".format(it.metricValue)
            }
            metric["cpu.user.percentage"]?.let {
                val sp = SpannableStringBuilder()
                sp.append("%.1f %%".format(it.metricValue))
                sp.setSpan(TextAppearanceSpan(fragment.context, R.style.HostMetricUnit),
                        sp.length - 1, sp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                cpu.text = sp
            }
            metric["memory.used"]?.let {
                var unit = "MB"
                var mem = (it.metricValue ?: 0.0f) / 1024.0f / 1024.0f
                if (mem > 999) {
                    unit = "GB"
                    mem /= 1024.0f
                }
                val sp = SpannableStringBuilder()
                if (mem > 999) {
                    sp.append("999+ %s".format(unit))
                } else {
                    sp.append("%.0f %s".format(mem, unit))
                }
                sp.setSpan(TextAppearanceSpan(fragment.context, R.style.HostMetricUnit),
                        sp.length - 2, sp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                memory.text = sp
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var view = LayoutInflater.from(fragment.context).inflate(R.layout.list_item_host, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val cardView: View by bindView(R.id.card_view)

        val name: TextView by bindView(R.id.name)
        val hostId: TextView by bindView(R.id.host_id)
        val detail: TextView by bindView(R.id.detail)
        val role: TextView by bindView(R.id.role)

        val health: View by bindView(R.id.health)

        val loadavg: View by bindView(R.id.loadavg)
        val cpu: View by bindView(R.id.cpu)
        val memory: View by bindView(R.id.memory)

        val detailButton: Button by bindView(R.id.detail_button)
    }
}