package com.hotpodata.common.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.hotpodata.common.R
import com.hotpodata.common.enums.HotPoDataApps
import timber.log.Timber
import java.util.*

/**
 * Created by jdrotos on 1/13/16.
 */
open class App(val id: HotPoDataApps, val iconResId: Int, val name: String, val desc: String, val applicationId: String, val privacyPolicyUrl: String? = null, val proApplicationId: String? = "") {
    object Factory {
        fun genApps(context: Context, exclude: HotPoDataApps): List<App> {
            var apps = ArrayList<App>()
            for (app in HotPoDataApps.values()) {
                if (app != exclude) {
                    apps.add(createApp(context, app))
                }
            }
            return apps
        }

        fun createApp(context: Context, app: HotPoDataApps): App {
            var icon = when (app) {
                HotPoDataApps.BACONMASHER -> R.mipmap.launcher_baconmasher
                HotPoDataApps.BLOCKELGANGER -> R.mipmap.launcher_blockelganger
                HotPoDataApps.FILECAT -> R.mipmap.launcher_filecat
                HotPoDataApps.REDCHAIN -> R.mipmap.launcher_redchain
                HotPoDataApps.TWISTRIS -> R.mipmap.launcher_twistris
                HotPoDataApps.WIKICAT -> R.mipmap.launcher_wikicat
            }
            var name = when (app) {
                HotPoDataApps.BACONMASHER -> context.getString(R.string.baconmasher)
                HotPoDataApps.BLOCKELGANGER -> context.getString(R.string.blockelganger)
                HotPoDataApps.FILECAT -> context.getString(R.string.filecat)
                HotPoDataApps.REDCHAIN -> context.getString(R.string.redchain)
                HotPoDataApps.TWISTRIS -> context.getString(R.string.twistris)
                HotPoDataApps.WIKICAT -> context.getString(R.string.wikicat)
            }
            var desc = when (app) {
                HotPoDataApps.BACONMASHER -> context.getString(R.string.baconmasher_desc)
                HotPoDataApps.BLOCKELGANGER -> context.getString(R.string.blockelganger_desc)
                HotPoDataApps.FILECAT -> context.getString(R.string.filecat_desc)
                HotPoDataApps.REDCHAIN -> context.getString(R.string.redchain_desc)
                HotPoDataApps.TWISTRIS -> context.getString(R.string.twistris_desc)
                HotPoDataApps.WIKICAT -> context.getString(R.string.wikicat_desc)
            }
            var applicationId = when (app) {
                HotPoDataApps.BACONMASHER -> context.getString(R.string.baconmasher_appId)
                HotPoDataApps.BLOCKELGANGER -> context.getString(R.string.blockelganger_appId)
                HotPoDataApps.FILECAT -> context.getString(R.string.filecat_appId)
                HotPoDataApps.REDCHAIN -> context.getString(R.string.redchain_appId)
                HotPoDataApps.TWISTRIS -> context.getString(R.string.twistris_appId)
                HotPoDataApps.WIKICAT -> context.getString(R.string.wikicat_appId)
            }
            var proId = when (app) {
                HotPoDataApps.BACONMASHER -> context.getString(R.string.baconmasherPro_appId)
                HotPoDataApps.FILECAT -> context.getString(R.string.filecatPro_appId)
                HotPoDataApps.REDCHAIN -> context.getString(R.string.redchainPro_appId)
                HotPoDataApps.WIKICAT -> context.getString(R.string.wikicatPro_appId)
                else -> ""
            }
            return App(app, icon, name, desc, applicationId, proId)
        }
    }

    fun firePlayStoreIntent(context: Context, pro: Boolean = false): Boolean {
        try {
            var intent = if (pro) genPlayStoreIntent(context) else genPlayStoreProIntent(context)
            if (intent != null) {
                context.startActivity(intent)
                return true
            }
        } catch(ex: Exception) {
            Timber.e(ex, "Failure to launch market intent")
        }
        return false
    }

    fun genPlayStoreIntent(context: Context): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(context.getString(R.string.market_intent_TEMPLATE, applicationId)))
        return intent
    }

    fun genPlayStoreProIntent(context: Context): Intent? {
        if (!TextUtils.isEmpty(proApplicationId)) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(context.getString(R.string.market_intent_TEMPLATE, proApplicationId)))
            return intent
        }
        return null
    }

    fun genPrivacyPolicyIntent(context: Context): Intent {
        val i = Intent(Intent.ACTION_VIEW)
        if (!TextUtils.isEmpty(privacyPolicyUrl)) {
            i.setData(Uri.parse(privacyPolicyUrl))
        }
        return i
    }

    fun firePrivacyPolicyIntent(context: Context): Boolean {
        try {
            if (!TextUtils.isEmpty(privacyPolicyUrl)) {
                context.startActivity(genPrivacyPolicyIntent(context))
                return true
            }
        } catch(ex: Exception) {
            Timber.e(ex, "Failure to firePrivacyPolicyIntent")
        }
        return false

    }


}