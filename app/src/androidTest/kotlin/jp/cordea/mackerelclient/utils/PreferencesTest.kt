package jp.cordea.mackerelclient.utils

import android.test.InstrumentationTestCase
import jp.cordea.mackerelclient.model.Preferences
import org.junit.Assert
import org.junit.Test

class PreferencesTest() : InstrumentationTestCase() {

    @Test
    public fun testUserId() {
        val context = instrumentation.context
        val pref = Preferences(context)
        Assert.assertEquals(-1, pref.userId)
        pref.userId = 1
        Assert.assertEquals(1, pref.userId)
        pref.clear()
        Assert.assertEquals(-1, pref.userId)
    }
}
