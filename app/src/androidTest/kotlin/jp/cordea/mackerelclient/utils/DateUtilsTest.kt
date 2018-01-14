package jp.cordea.mackerelclient.utils

import android.test.AndroidTestCase
import org.junit.Test
import java.util.*

class DateUtilsTest() : AndroidTestCase() {

    @Test
    public fun testStringFromEpoch() {
        val oldTz = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"))
        assertEquals("00:00:00", DateUtils.stringFromEpoch(1458486000))
        assertEquals("23:59:59", DateUtils.stringFromEpoch(1458572399))
        TimeZone.setDefault(oldTz)
    }

    @Test
    public fun testStringDateFromEpoch() {
        val oldTz = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"))
        assertEquals("Mar 21, 2016 12:00:00 AM", DateUtils.stringDateFromEpoch(1458486000))
        assertEquals("Mar 21, 2016 11:59:59 PM", DateUtils.stringDateFromEpoch(1458572399))
        TimeZone.setDefault(oldTz)
    }
}
