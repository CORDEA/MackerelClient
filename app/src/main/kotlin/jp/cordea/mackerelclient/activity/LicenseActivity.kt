package jp.cordea.mackerelclient.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import butterknife.bindView
import jp.cordea.mackerelclient.viewmodel.LicenseViewModel
import jp.cordea.mackerelclient.R

class LicenseActivity : AppCompatActivity() {

    private val viewModel by lazy {
        LicenseViewModel(this)
    }

    val toolbar: Toolbar by bindView(R.id.toolbar)

    val licenseView: TextView by bindView(R.id.license)

    val container: View by bindView(R.id.container)

    val progress: View by bindView(R.id.progress)

    val error: View by bindView(R.id.error)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)
        setSupportActionBar(toolbar)

        viewModel.licensesObservable
                .subscribe({
                    licenseView.text = it
                    container.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                }, {
                    error.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                    it.printStackTrace()
                })
    }

}
