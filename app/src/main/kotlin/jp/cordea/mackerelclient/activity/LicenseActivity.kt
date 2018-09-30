package jp.cordea.mackerelclient.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import jp.cordea.mackerelclient.R
import jp.cordea.mackerelclient.databinding.ActivityLicenseBinding
import jp.cordea.mackerelclient.viewmodel.LicenseViewModel

class LicenseActivity : AppCompatActivity() {

    private val viewModel by lazy {
        LicenseViewModel(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
                .setContentView<ActivityLicenseBinding>(this, R.layout.activity_license)
        setSupportActionBar(binding.toolbar)

        val content = binding.content
        viewModel.licensesObservable
                .subscribe({
                    content.licenseTextView.text = it
                    content.container.visibility = View.VISIBLE
                    content.progressLayout.visibility = View.GONE
                }, {
                    content.errorLayout.root.visibility = View.VISIBLE
                    content.progressLayout.visibility = View.GONE
                    it.printStackTrace()
                })
    }

}
