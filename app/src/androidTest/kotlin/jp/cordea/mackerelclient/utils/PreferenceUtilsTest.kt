package jp.cordea.mackerelclient.utils

import android.test.InstrumentationTestCase
import org.junit.Test

/**
 * Created by CORDEA on 2016/03/21.
 */
class PreferenceUtilsTest() : InstrumentationTestCase() {

    @Test
    public fun testUserId() {
        val context = instrumentation.context
        assert(-1 == PreferenceUtils.readUserId(context))
        PreferenceUtils.writeUserId(context, 1)
        assert(1 == PreferenceUtils.readUserId(context))
        PreferenceUtils.removeUserId(context)
        assert(-1 == PreferenceUtils.readUserId(context))
    }
}
