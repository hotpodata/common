package com.hotpodata.common.utils

import timber.log.Timber
import java.security.MessageDigest

/**
 * Created by jdrotos on 1/15/16.
 */
object HashUtils {
    fun md5(s: String): String {
        try {
            var digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            var messageDigest = digest.digest()

            var hexString = StringBuffer()
            for (i in messageDigest.indices) {
                var h = Integer.toHexString(0xFF and messageDigest[i].toInt())
                while (h.length < 2)
                    h = "0" + h
                hexString.append(h)
            }
            return hexString.toString()
        } catch(ex: Exception) {
            Timber.e(ex, "Fail in md5");
        }
        return ""
    }
}