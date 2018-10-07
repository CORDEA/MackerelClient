package jp.cordea.mackerelclient.viewmodel

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.cordea.mackerelclient.R
import java.io.BufferedReader
import java.io.InputStreamReader

class LicenseViewModel(private val context: Context) {

    val licensesObservable: Single<SpannableStringBuilder>
        get() = Single
            .just(R.raw.licenses)
            .subscribeOn(Schedulers.io())
            .map {
                BufferedReader(InputStreamReader(context.resources.openRawResource(it)))
            }
            .map {
                val lines = SpannableStringBuilder()
                var line = it.readLine()
                while (line != null) {
                    val st = lines.length
                    if (line.length > 6 && line.substring(0, 6) == "title:") {
                        line = line.removeRange(0, 6)
                        lines.append(line + "\n")
                        val style = TextAppearanceSpan(context, R.style.LicenseTitle)
                        lines.setSpan(
                            style,
                            st,
                            st + line.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    } else {
                        val style = TextAppearanceSpan(context, R.style.LicenseContent)
                        lines.append(line + "\n")
                        lines.setSpan(
                            style,
                            st,
                            st + line.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    line = it.readLine()
                }
                lines
            }
            .observeOn(AndroidSchedulers.mainThread())
}
