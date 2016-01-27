package com.hotpodata.common.utils

import android.content.Context
import android.content.pm.PackageManager
import timber.log.Timber

/**
 * Created by jdrotos on 1/27/16.
 */
object AndroidUtils {
    fun getVersionName(ctx: Context): String {
        return try {
            val pInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e, "Version fail")
            "?"
        }
    }

    fun getVersionCode(ctx: Context): Int {
        return try {
            val pInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
            pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e, "Version fail")
            0
        }
    }
}