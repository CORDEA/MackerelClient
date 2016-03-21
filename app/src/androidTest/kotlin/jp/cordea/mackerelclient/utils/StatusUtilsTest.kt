package jp.cordea.mackerelclient.utils

import android.support.v4.content.ContextCompat
import android.test.InstrumentationTestCase
import jp.cordea.mackerelclient.R
import org.junit.Test

/**
 * Created by CORDEA on 2016/03/21.
 */
class StatusUtilsTest() {
    @Test
    public fun testStringToRequestName() {
        assert("poweroff" == StatusUtils.stringToRequestName("Power off"))
        assert("custom status" == StatusUtils.stringToRequestName("Custom Status"))
    }

    @Test
    public fun testRequestNameToString() {
        assert("Working" == StatusUtils.requestNameToString("working"))
        assert("Standby" == StatusUtils.requestNameToString("standby"))
        assert("Maintenance" == StatusUtils.requestNameToString("maintenance"))
        assert("Power off" == StatusUtils.requestNameToString("poweroff"))
        assert("Custom status" == StatusUtils.requestNameToString("Custom status"))
    }

    @Test
    public fun testStringToStatusColor() {
        assert(R.color.statusWorking == StatusUtils.stringToStatusColor("working"))
        assert(R.color.statusStandby == StatusUtils.stringToStatusColor("standby"))
        assert(R.color.statusMaintenance == StatusUtils.stringToStatusColor("maintenance"))
        assert(R.color.statusPoweroff == StatusUtils.stringToStatusColor("poweroff"))
        assert(R.color.statusPoweroff == StatusUtils.stringToStatusColor("customstatus"))
    }
}
