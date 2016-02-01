package jp.cordea.mackerelclient.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.View
import android.widget.TextView
import butterknife.bindView
import jp.cordea.mackerelclient.R
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.BufferedReader
import java.io.InputStreamReader

class LicenseActivity : AppCompatActivity() {

    val toolbar: Toolbar by bindView(R.id.toolbar)
    val licenseView: TextView by bindView(R.id.license)
    val container: View by bindView(R.id.container)

    val progress: View by bindView(R.id.progress)
    val error: View by bindView(R.id.error)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)
        setSupportActionBar(toolbar)

        Observable
                .just(R.raw.licenses)
                .subscribeOn(Schedulers.newThread())
                .map {
                    BufferedReader(InputStreamReader(resources.openRawResource(it)))
                }
                .map {
                    var lines = SpannableStringBuilder()
                    var line = it.readLine()
                    while (line != null) {
                        val st = lines.length
                        if (line.length > 6 && line.substring(0, 6) == "title:") {
                            line = line.removeRange(0, 6)
                            lines.append(line + "\n")
                            val style = TextAppearanceSpan(this, R.style.LicenseTitle)
                            lines.setSpan(style, st, st + line.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        } else {
                            val style = TextAppearanceSpan(this, R.style.LicenseContent)
                            lines.append(line + "\n")
                            lines.setSpan(style, st, st + line.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        line = it.readLine()
                    }
                    lines
                }
                .observeOn(AndroidSchedulers.mainThread())
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
