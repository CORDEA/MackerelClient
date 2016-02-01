package jp.cordea.mackerelclient.utils

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by CORDEA on 2016/01/17.
 */
class GravatarUtils {
    companion object {

        private fun hex(arr: ByteArray): String {
            val sb = StringBuffer()
            for (it in arr) {
                sb.append(Integer.toHexString((it.toInt() and 0xff) or 0x100).substring(1,3))
            }
            return sb.toString()
        }

        private fun md5Hex (message: String): String? {
            try {
                val md = MessageDigest.getInstance("MD5")
                return hex (md.digest(message.toByteArray("CP1252")))
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return null
        }

        public fun getGravatarImage(email: String, size: Int): String? {
            md5Hex(email)?.let {
                return "http://www.gravatar.com/avatar/%s?s=%d".format(it, size)
            }
            return null
        }
    }
}
