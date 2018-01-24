package jp.cordea.mackerelclient.adapter

import android.app.Activity
import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import io.realm.Realm
import jp.cordea.mackerelclient.MemoryValueFormatter
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.MetricsEditActivity
import jp.cordea.mackerelclient.model.MetricsParameter
import jp.cordea.mackerelclient.model.UserMetric
import kotterknife.bindView
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class MetricsAdapter(
        val activity: Activity,
        val items: MutableList<MetricsParameter>,
        val type: MetricsType,
        val id: String,
        private var visibles: Int = 0,
        private var canRefresh: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val lock = ReentrantLock()

    private var drawComplete: Int = 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as? ViewHolder)?.let {
            if (items[position].label.isEmpty()) {
                it.title.visibility = View.GONE
            } else {
                it.title.text = items[position].label
            }
            val lineData = items[position].data
            if (lineData == null) {
                if (items[position].isError) {
                    it.progress.visibility = View.GONE
                    it.error.visibility = View.VISIBLE
                }
            } else {
                it.chart.apply {
                    data = lineData
                    setDescription("")
                    xAxis.position = XAxis.XAxisPosition.BOTTOM

                    val needFormat = lineData.dataSets
                            .filter {
                                "memory" == it.label.split(".")[0]
                            }
                            .size == lineData.dataSets.size
                    if (needFormat) {
                        val format = context.resources.getString(R.string.metrics_data_gb_format)
                        data.dataSets[0].label = format.format(data.dataSets[0].label)
                        if (data.dataSets.size > 1) {
                            data.dataSets[1].label = format.format(data.dataSets[1].label)
                        }
                        axisRight.valueFormatter = MemoryValueFormatter()
                        axisLeft.valueFormatter = MemoryValueFormatter()
                    }

                    axisRight.setLabelCount(3, false)
                    axisLeft.setLabelCount(3, false)
                    visibility = View.VISIBLE
                    it.progress.visibility = View.GONE
                    invalidate()
                }
                ++visibles
                canRefresh = visibles == itemCount
            }

            it.edit.setOnClickListener {
                val intent = MetricsEditActivity
                        .createIntent(activity, type, id, items[position].id)
                activity.startActivityForResult(intent, MetricsEditActivity.RequestCode)
            }

            it.delete.setOnClickListener {
                AlertDialog
                        .Builder(activity)
                        .setMessage(R.string.metrics_card_delete_dialog_title)
                        .setPositiveButton(R.string.button_positive, { _, _ ->
                            lock.withLock {
                                val realm = Realm.getDefaultInstance()
                                realm.executeTransaction {
                                    realm.where(UserMetric::class.java)
                                            .equalTo("id", items[position].id)
                                            .findFirst()
                                            .deleteFromRealm()
                                }
                                realm.close()
                                items.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeRemoved(position, items.size)
                                --drawComplete
                            }
                        }).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(activity)
                .inflate(R.layout.list_item_metrics_chart, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int =
            items.size

    fun refreshRecyclerViewItem(item: Pair<Int, LineData?>, drawCompleteMetrics: Int): Int {
        drawComplete = drawCompleteMetrics
        lock.withLock {
            val idx = items.map { it.id }.indexOf(item.first)
            if (idx != -1 && idx < items.size) {
                items[idx] = MetricsParameter(
                        item.first,
                        item.second,
                        items[idx].label,
                        item.second == null
                )
                notifyDataSetChanged()
                return ++drawComplete
            }
        }
        return drawComplete
    }

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val progress: View by bindView(R.id.progress)

        val chart: LineChart by bindView(R.id.chart)

        val title: TextView by bindView(R.id.title)

        val delete: Button by bindView(R.id.delete)

        val edit: Button by bindView(R.id.edit)

        val error: View by bindView(R.id.error)

    }
}
