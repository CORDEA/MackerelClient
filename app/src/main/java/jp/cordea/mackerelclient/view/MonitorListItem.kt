package jp.cordea.mackerelclient.view

import android.content.Context
import androidx.fragment.app.Fragment
import com.xwray.groupie.databinding.BindableItem
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.activity.MonitorDetailActivity
import jp.cordea.mackerelclient.api.response.MonitorDataResponse
import jp.cordea.mackerelclient.databinding.ListItemMonitorBinding
import javax.inject.Inject

class MonitorListItem @Inject constructor(
    private val fragment: Fragment
) : BindableItem<ListItemMonitorBinding>() {
    private lateinit var model: MonitorListItemModel

    fun update(model: MonitorListItemModel) = apply { this.model = model }

    override fun getLayout(): Int = R.layout.list_item_monitor

    override fun bind(binding: ListItemMonitorBinding, position: Int) {
        binding.model = model
        binding.root.setOnClickListener {
            val intent = MonitorDetailActivity.createIntent(fragment.context!!, model.response)
            fragment.startActivityForResult(intent, MonitorDetailActivity.REQUEST_CODE)
        }
    }
}

class MonitorListItemModel(
    val response: MonitorDataResponse,
    defaultName: String
) {
    companion object {
        fun from(context: Context, response: MonitorDataResponse) =
            MonitorListItemModel(response, context.getString(R.string.na_text))
    }

    val type: String = response.type
    val id: String = response.id
    val name: String = response.name ?: defaultName
}
