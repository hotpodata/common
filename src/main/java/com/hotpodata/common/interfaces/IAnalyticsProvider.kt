package com.hotpodata.common.interfaces

import android.content.Context
import com.google.android.gms.analytics.Tracker

/**
 * Created by jdrotos on 1/20/16.
 */
interface IAnalyticsProvider {
    fun getTracker(context: Context): Tracker
}