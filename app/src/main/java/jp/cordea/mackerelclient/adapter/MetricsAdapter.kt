package jp.cordea.mackerelclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import jp.cordea.mackerelclient.MemoryValueFormatter
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.MetricsEditActivity
import jp.cordea.mackerelclient.databinding.ListItemMetricsChartBinding
import jp.cordea.mackerelclient.fragment.MetricsDeleteConfirmDialogFragment
import jp.cordea.mackerelclient.model.MetricsLineDataSet

class MetricsAdapter(
    private val activity: AppCompatActivity,
    private val type: MetricsType,
    private val id: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<MetricsLineDataSet>()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ViewHolder)?.binding?.let { binding ->
            val item = items[position]
            when (item) {
                is MetricsLineDataSet.Success -> {
                    if (item.label.isNullOrBlank()) {
                        binding.titleTextView.visibility = View.GONE
                    } else {
                        binding.titleTextView.text = item.label
                    }
                    setLineData(binding, item.data)
                }
                is MetricsLineDataSet.Failure -> {
                    binding.titleTextView.visibility = View.GONE
                    binding.lineChart.isVisible = false
                    binding.error.root.isVisible = true
                }
            }
            binding.editButton.setOnClickListener {
                val intent = MetricsEditActivity
                    .createIntent(activity, type, id, items[position].id)
                activity.startActivityForResult(intent, MetricsEditActivity.REQUEST_CODE)
            }
            binding.deleteButton.setOnClickListener {
                MetricsDeleteConfirmDialogFragment.newInstance(items[position].id)
                    .show(activity.supportFragmentManager, MetricsDeleteConfirmDialogFragment.TAG)
            }
        }
    }

    private fun setLineData(binding: ListItemMetricsChartBinding, lineData: LineData) {
        binding.lineChart.apply {
            setDescription("")
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            if (lineData.needFormat) {
                val format = context.resources.getString(R.string.metrics_data_gb_format)
                lineData.dataSets[0].label = format.format(lineData.dataSets[0].label)
                if (lineData.dataSets.size > 1) {
                    lineData.dataSets[1].label = format.format(lineData.dataSets[1].label)
                }
                axisRight.valueFormatter = MemoryValueFormatter()
                axisLeft.valueFormatter = MemoryValueFormatter()
            }

            axisRight.setLabelCount(3, false)
            axisLeft.setLabelCount(3, false)
            data = lineData
            invalidate()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(activity).inflate(R.layout.list_item_metrics_chart, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    fun add(item: MetricsLineDataSet) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun removeAt(id: Int) {
        val index = items.indexOfFirst { it.id == id }
        items.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, itemCount - index)
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
