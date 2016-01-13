package com.hotpodata.common.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.hotpodata.common.R
import com.hotpodata.common.enums.Libraries
import com.hotpodata.common.enums.Licenses
import java.util.*

/**
 * Created by jdrotos on 1/13/16.
 */
abstract class Acknowledgement(val displayName: String, val licenseName: String) {
    object Factory {
        fun genAcknowledgements(context: Context, vararg libraries: Libraries): List<Acknowledgement> {
            var acks = ArrayList<Acknowledgement>()
            for (lib in libraries) {
                acks.add(createAcknowledgement(context, lib))
            }
            return acks
        }

        fun createAcknowledgement(context: Context, library: Libraries): Acknowledgement {
            var libDisplayName: String = when (library) {
                Libraries.AutoFitTextView -> context.getString(R.string.autofittextview)
                Libraries.RxAndroid -> context.getString(R.string.rxandroid)
                Libraries.RxJava -> context.getString(R.string.rxjava)
                Libraries.RxKotlin -> context.getString(R.string.rxkotlin)
                Libraries.Timber -> context.getString(R.string.timber)
            }

            var libActionUrl: String = when (library) {
                Libraries.AutoFitTextView -> context.getString(R.string.autofittextview_url)
                Libraries.RxAndroid -> context.getString(R.string.rxandroid_url)
                Libraries.RxJava -> context.getString(R.string.rxjava_url)
                Libraries.RxKotlin -> context.getString(R.string.rxkotlin_url)
                Libraries.Timber -> context.getString(R.string.timber_url)
            }

            var licenseDisplayname = when (getLicenseForLibrary(library)) {
                Licenses.Apache2 -> context.getString(R.string.license_apache)
                Licenses.MIT -> context.getString(R.string.license_mit)
                else -> ""
            }

            return object : Acknowledgement(libDisplayName, licenseDisplayname) {
                override fun genActionIntent(context: Context): Intent {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.setData(Uri.parse(libActionUrl))
                    return i
                }
            }
        }

        fun getLicenseForLibrary(library: Libraries): Licenses {
            //TODO: Map to other licenses
            return Licenses.Apache2
        }
    }

    abstract fun genActionIntent(context: Context): Intent

    fun fireActionIntent(context: Context) {
        context.startActivity(genActionIntent(context))
    }
}