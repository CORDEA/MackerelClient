package jp.cordea.mackerelclient.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.Lazy
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.SerialDisposable
import jp.cordea.mackerelclient.ListItemDecoration
import jp.cordea.mackerelclient.MetricsType
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.adapter.MetricsAdapter
import jp.cordea.mackerelclient.databinding.ActivityMetricsBinding
import jp.cordea.mackerelclient.databinding.ContentMetricsBinding
import jp.cordea.mackerelclient.fragment.MetricsDeleteConfirmDialogFragment
import jp.cordea.mackerelclient.viewmodel.MetricsViewModel
import javax.inject.Inject

class MetricsActivity : AppCompatActivity(),
    HasSupportFragmentInjector,
    MetricsDeleteConfirmDialogFragment.OnDeleteMetricsListener {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelProvider: Lazy<MetricsViewModel>

    private val hostId by lazy { intent.getStringExtra(HOST_ID_KEY) }

    private val disposable = SerialDisposable()
    private val viewModel get() = viewModelProvider.get()

    private var enableRefresh = false

    private lateinit var adapter: MetricsAdapter
    private lateinit var contentBinding: ContentMetricsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityMetricsBinding>(this, R.layout.activity_metrics)
        contentBinding = binding.content
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        contentBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        contentBinding.swipeRefresh.setOnRefreshListener {
            if (enableRefresh) {
                refresh(true)
            }
            contentBinding.swipeRefresh.isRefreshing = false
        }
        contentBinding.templateButton.setOnClickListener {
            contentBinding.templateButton.isClickable = false
            viewModel.setDefaultUserMetrics(hostId)
            refresh(true)
        }
        adapter = MetricsAdapter(this, MetricsType.HOST, hostId)
        contentBinding.recyclerView.adapter = adapter
        contentBinding.recyclerView.addItemDecoration(ListItemDecoration(this))

        if (savedInstanceState == null) {
            viewModel.start(hostId)
        }

        refresh(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.metric, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MetricsEditActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                refresh(true)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_add -> {
                val intent = MetricsEditActivity.createIntent(this, MetricsType.HOST, hostId)
                startActivityForResult(intent, MetricsEditActivity.REQUEST_CODE)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDelete(id: Int) {
        viewModel.removeBy(id)
        refresh(false)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    private fun refresh(forceRefresh: Boolean) {
        enableRefresh = false
        adapter.clear()
        if (viewModel.isExistsMetricsDefinition) {
            contentBinding.noticeContainer.isVisible = false
            contentBinding.progress.isVisible = true
            contentBinding.swipeRefresh.isVisible = true
        } else {
            contentBinding.noticeContainer.isVisible = true
            contentBinding.progress.isVisible = false
            contentBinding.swipeRefresh.isVisible = false
            return
        }
        viewModel
            .fetchMetrics(forceRefresh)
            .subscribe({
                adapter.add(it)
            }, {}, {
                contentBinding.progress.isVisible = false
                enableRefresh = true
            })
            .run(disposable::set)
    }

    companion object {
        private const val HOST_ID_KEY = "HostIdKey"

        fun createIntent(context: Context, hostId: String): Intent =
            Intent(context, MetricsActivity::class.java).apply {
                putExtra(HOST_ID_KEY, hostId)
            }
    }
}
