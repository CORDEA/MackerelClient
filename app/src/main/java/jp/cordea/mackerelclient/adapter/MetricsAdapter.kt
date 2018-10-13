package jp.cordea.mackerelclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import jp.cordea.mackerelclient.MemoryValueFormatter
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.MetricsEditActivity
import jp.cordea.mackerelclient.databinding.ListItemMetricsChartBinding
import jp.cordea.mackerelclient.fragment.MetricsDeleteConfirmDialogFragment
import jp.cordea.mackerelclient.model.MetricsParameter
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class MetricsAdapter(
    private val activity: AppCompatActivity,
    items: List<MetricsParameter>,
    private val type: MetricsType,
    private val id: String,
    private var visibles: Int = 0,
    private var canRefresh: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val lock = ReentrantLock()
    private val items: MutableList<MetricsParameter> = items.toMutableList()

    private var drawComplete: Int = 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ViewHolder)?.binding?.let { binding ->
            if (items[position].label.isEmpty()) {
                binding.titleTextView.visibility = View.GONE
            } else {
                binding.titleTextView.text = items[position].label
            }
            val lineData = items[position].data
            if (lineData == null) {
                if (items[position].isError) {
                    binding.progressLayout.visibility = View.GONE
                    binding.error.root.visibility = View.VISIBLE
                }
            } else {
                setLineData(binding, lineData)
                ++visibles
                canRefresh = visibles == itemCount
            }

            binding.editButton.setOnClickListener {
                val intent = MetricsEditActivity
                    .createIntent(activity, type, id, items[position].id)
                activity.startActivityForResult(intent, MetricsEditActivity.REQUEST_CODE)
            }

            binding.deleteButton.setOnClickListener {
                MetricsDeleteConfirmDialogFragment.newInstance(items, position)
                    .show(activity.supportFragmentManager, MetricsDeleteConfirmDialogFragment.TAG)
            }
        }
    }

    private fun setLineData(binding: ListItemMetricsChartBinding, lineData: LineData) {
        binding.lineChart.apply {
            data = lineData
            setDescription("")
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            if (data.needFormat) {
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
            binding.progressLayout.visibility = View.GONE
            invalidate()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(activity)
            .inflate(R.layout.list_item_metrics_chart, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    fun refreshRecyclerViewItem(item: Pair<Int, LineData?>, drawCompleteMetrics: Int): Int {
        drawComplete = drawCompleteMetrics
        lock.withLock {
            val idx = items.indexOfFirst { it.id == item.first }
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

    fun removeAt(position: Int) {
        lock.withLock {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeRemoved(position, items.size)
            --drawComplete
        }
    }

    private val LineData.needFormat: Boolean
        get() =
            this.dataSets
                .filter { "memory" == it.label.split(".")[0] }
                .size == this.dataSets.size

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ListItemMetricsChartBinding = ListItemMetricsChartBinding.bind(view)
    }
}
