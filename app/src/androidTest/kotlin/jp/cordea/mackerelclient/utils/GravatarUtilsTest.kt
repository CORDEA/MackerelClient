package jp.cordea.mackerelclient.utils

import org.junit.Test

class GravatarUtilsTest {

    @Test
    public fun testGetGravatarImage() {
        assert("http://www.gravatar.com/avatar/f9879d71855b5ff21e4963273a886bfc?s=64" ==
                GravatarUtils.getGravatarImage("MyEmailAddress@example.com", 64))
    }
}
