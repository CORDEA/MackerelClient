package jp.cordea.mackerelclient

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LongExtKtTest {
    private val context: Context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun test() {
        Truth.assertThat(0L.toRelativeTime(context, 0 * 1000L)).isEqualTo("0 秒前")
        Truth.assertThat(0L.toRelativeTime(context, 59 * 1000L)).isEqualTo("59 秒前")
        Truth.assertThat(0L.toRelativeTime(context, 60 * 1000L)).isEqualTo("1 分前")
        Truth.assertThat(0L.toRelativeTime(context, 59 * 60 * 1000L)).isEqualTo("59 分前")
        Truth.assertThat(0L.toRelativeTime(context, 60 * 60 * 1000L)).isEqualTo("1 時間前")
        Truth.assertThat(0L.toRelativeTime(context, 23 * 60 * 60 * 1000L)).isEqualTo("23 時間前")
        Truth.assertThat(0L.toRelativeTime(context, 24 * 60 * 60 * 1000L)).isEqualTo("1 日前")
        Truth.assertThat(0L.toRelativeTime(context, 9 * 24 * 60 * 60 * 1000L)).isEqualTo("9 日前")
        Truth.assertThat(0L.toRelativeTime(context, 10 * 24 * 60 * 60 * 1000L)).isEqualTo("10 日以上前")
    }
}
