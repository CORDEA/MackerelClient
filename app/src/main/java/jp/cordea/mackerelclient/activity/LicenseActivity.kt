package jp.cordea.mackerelclient.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.reactivex.disposables.Disposable
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.databinding.ActivityLicenseBinding
import jp.cordea.mackerelclient.viewmodel.LicenseViewModel

class LicenseActivity : AppCompatActivity() {

    private val viewModel by lazy { LicenseViewModel(this) }

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityLicenseBinding>(this, R.layout.activity_license)

        disposable = viewModel.licensesObservable
            .subscribe({
                binding.licenseTextView.text = it
                binding.container.visibility = View.VISIBLE
                binding.progress.visibility = View.GONE
            }, {
                binding.errorLayout.root.visibility = View.VISIBLE
                binding.progress.visibility = View.GONE
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
