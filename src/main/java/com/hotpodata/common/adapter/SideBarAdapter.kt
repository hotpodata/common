package com.hotpodata.common.adapter

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.analytics.HitBuilders
import com.hotpodata.common.R
import com.hotpodata.common.adapter.viewholder.*
import com.hotpodata.common.data.Acknowledgement
import com.hotpodata.common.data.App
import com.hotpodata.common.data.Contact
import com.hotpodata.common.enums.Libraries
import com.hotpodata.common.interfaces.IAnalyticsProvider
import com.hotpodata.common.utils.AndroidUtils
import timber.log.Timber
import java.util.*

/**
 * Created by jdrotos on 11/7/15.
 */
abstract class SideBarAdapter(val ctx: Context, val analyticsProvider: IAnalyticsProvider?, val app: App, val isPro: Boolean, val showGoPro: Boolean, vararg libs: Libraries) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val CATEGORY_SIDEBAR = "SideBar"
    val ACTION_RATE_APP = "Rate_App"
    val ACTION_GO_PRO = "Go_Pro"
    val ACTION_CONTACT = "Contact"
    val ACTION_OTHER_APP = "Other_App"
    val ACTION_ACK = "Acknowledgement"
    val ACTION_PRIVACY_POLICY = "Privacy_Policy"

    private val ROW_TYPE_HEADER = 0
    private val ROW_TYPE_ONE_LINE = 1
    private val ROW_TYPE_TWO_LINE = 2
    private val ROW_TYPE_DIV = 3
    private val ROW_TYPE_DIV_INSET = 4
    private val ROW_TYPE_SIDE_BAR_HEADING = 5

    protected var mRows: List<Any> = ArrayList()
    protected var mColor = Color.MAGENTA
    private var mLibs: MutableList<Libraries>

    init {
        mLibs = ArrayList<Libraries>()
        mLibs.addAll(libs)
    }

    public fun setAccentColor(color: Int) {
        mColor = color;
        rebuildRowSet()
    }

    public fun rebuildRowSet() {
        mRows = buildRows()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ROW_TYPE_HEADER -> {
                val v = inflater.inflate(R.layout.row_sidebar_section_header, parent, false)
                SideBarSectionHeaderViewHolder(v)
            }
            ROW_TYPE_ONE_LINE -> {
                val v = inflater.inflate(R.layout.row_text_one_line, parent, false)
                RowTextOneLineViewHolder(v)
            }
            ROW_TYPE_TWO_LINE -> {
                val v = inflater.inflate(R.layout.row_text_two_line, parent, false)
                RowTextTwoLineViewHolder(v)
            }
            ROW_TYPE_DIV, ROW_TYPE_DIV_INSET -> {
                val v = inflater.inflate(R.layout.row_div, parent, false)
                RowDivViewHolder(v)
            }
            ROW_TYPE_SIDE_BAR_HEADING -> {
                val v = inflater.inflate(R.layout.row_sidebar_header, parent, false)
                SideBarHeaderViewHolder(v)
            }
            else -> null
        }
    }

    protected abstract fun genCustomRows(): List<Any>

    private fun buildRows(): List<Any> {
        var sideBarRows = ArrayList<Any>()

        //HEADER ROW
        var version = AndroidUtils.getVersionName(ctx)
        sideBarRows.add(SideBarAdapter.RowSideBarHeading(app.iconResId, app.name, version))


        //SUBCLASS ROWS
        var customRows = genCustomRows()
        sideBarRows.addAll(customRows)


        //ACTION AND CONTACT ROWS
        if (customRows.size > 0) {
            sideBarRows.add(RowDiv(false))
        }
        var actionRows = ArrayList<Any>()
        actionRows.add(SideBarAdapter.RowSettings(ctx.resources.getString(R.string.rate_us), ctx.resources.getString(R.string.rate_us_blerb_template, app.name), View.OnClickListener {
            app.firePlayStoreIntent(ctx, isPro)
            try {
                analyticsProvider?.getTracker(ctx)?.let {
                    it.send(HitBuilders.EventBuilder()
                            .setCategory(CATEGORY_SIDEBAR)
                            .setAction(ACTION_RATE_APP)
                            .build());
                }
            } catch(ex: Exception) {
                Timber.e(ex, "Error recording analytics event.")
            }
        }, R.drawable.ic_action_rate))
        if (!isPro && showGoPro) {
            actionRows.add(RowDiv(true))
            actionRows.add(SideBarAdapter.RowSettings(ctx.resources.getString(R.string.go_pro), ctx.resources.getString(R.string.go_pro_blurb), View.OnClickListener {
                app.firePlayStoreIntent(ctx, true)
                try {
                    analyticsProvider?.getTracker(ctx)?.let {
                        it.send(HitBuilders.EventBuilder()
                                .setCategory(CATEGORY_SIDEBAR)
                                .setAction(ACTION_GO_PRO)
                                .build());
                    }
                } catch(ex: Exception) {
                    Timber.e(ex, "Error recording analytics event.")
                }
            }, R.drawable.ic_local_atm_24dp))
        }
        var contacts = Contact.Factory.genContacts(ctx, app.name)
        for (contact in contacts) {
            actionRows.add(RowDiv(true))
            actionRows.add(SideBarAdapter.RowSettings(contact.name, contact.desc, View.OnClickListener {
                contact.fireIntent(ctx)
                try {
                    analyticsProvider?.getTracker(ctx)?.let {
                        it.send(HitBuilders.EventBuilder()
                                .setCategory(CATEGORY_SIDEBAR)
                                .setAction(ACTION_CONTACT)
                                .setLabel(contact.name)
                                .build());
                    }
                } catch(ex: Exception) {
                    Timber.e(ex, "Error recording analytics event.")
                }
            }, contact.iconResId))
        }
        if (actionRows.size > 0) {
            actionRows.add(0, ctx.resources.getString(R.string.actions))
        }
        sideBarRows.addAll(actionRows)


        //OUR OTHER APPS
        sideBarRows.add(RowDiv(false))
        var appRows = ArrayList<Any>()
        var apps = App.Factory.genApps(ctx, app.id)
        for (app in apps) {
            if (appRows.size > 0) {
                appRows.add(RowDiv(true))
            }
            appRows.add(SideBarAdapter.RowSettings(app.name, app.desc, View.OnClickListener {
                app.firePlayStoreIntent(ctx, false)
                try {
                    analyticsProvider?.getTracker(ctx)?.let {
                        it.send(HitBuilders.EventBuilder()
                                .setCategory(CATEGORY_SIDEBAR)
                                .setAction(ACTION_OTHER_APP)
                                .setLabel(app.name)
                                .build());
                    }
                } catch(ex: Exception) {
                    Timber.e(ex, "Error recording analytics event.")
                }
            }, app.iconResId))
        }
        if (appRows.size > 0) {
            appRows.add(0, ctx.resources.getString(R.string.apps_label))
        }
        sideBarRows.addAll(appRows)


        //ACKNOWLEDGEMENTS
        //OUR OTHER APPS
        sideBarRows.add(RowDiv(false))
        var ackRows = ArrayList<Any>()
        var acks = Acknowledgement.Factory.genAcknowledgements(ctx, mLibs)
        for (ack in acks) {
            ackRows.add(SideBarAdapter.RowSettings(ack.displayName, ack.licenseName, View.OnClickListener {
                ack.fireActionIntent(ctx)
                try {
                    analyticsProvider?.getTracker(ctx)?.let {
                        it.send(HitBuilders.EventBuilder()
                                .setCategory(CATEGORY_SIDEBAR)
                                .setAction(ACTION_ACK)
                                .setLabel(ack.displayName)
                                .build());
                    }
                } catch(ex: Exception) {
                    Timber.e(ex, "Error recording analytics event.")
                }
            }))
        }
        if (ackRows.size > 0) {
            ackRows.add(0, ctx.resources.getString(R.string.acknowledgements))
        }
        sideBarRows.addAll(ackRows)

        //LEGAL
        if (ackRows.size > 0) {
            sideBarRows.add(SideBarAdapter.RowDiv(false))
        }
        if (!TextUtils.isEmpty(app.privacyPolicyUrl)) {
            sideBarRows.add(SideBarAdapter.RowSettings(ctx.resources.getString(R.string.privacy_policy), ctx.resources.getString(R.string.privacy_policy_blurb), View.OnClickListener {
                app.firePrivacyPolicyIntent(ctx)
                try {
                    analyticsProvider?.getTracker(ctx)?.let {
                        it.send(HitBuilders.EventBuilder()
                                .setCategory(CATEGORY_SIDEBAR)
                                .setAction(ACTION_PRIVACY_POLICY)
                                .build());
                    }
                } catch(ex: Exception) {
                    Timber.e(ex, "Error recording analytics event.")
                }
            }))
        }
        sideBarRows.add(SideBarAdapter.RowSettings(ctx.resources.getString(R.string.legal_heading), ctx.resources.getString(R.string.legal_blurb), View.OnClickListener { }))

        return sideBarRows
    }


    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = getItemViewType(position)
        val objData = mRows[position]
        when (type) {
            ROW_TYPE_SIDE_BAR_HEADING -> {
                val vh = holder as SideBarHeaderViewHolder
                val data = objData as RowSideBarHeading
                vh.mIcon.setImageResource(data.iconResId)
                vh.mTitleTv.text = data.title
                vh.mSubTitleTv.text = data.subtitle
                vh.mContainer.setBackgroundColor(mColor)
            }
            ROW_TYPE_HEADER -> {
                val vh = holder as SideBarSectionHeaderViewHolder
                val data = objData as String
                vh.mTitleTv.text = data
            }
            ROW_TYPE_ONE_LINE -> {
                val vh = holder as RowTextOneLineViewHolder
                val data = objData as RowSettings
                vh.mTextOne.text = data.title
                vh.itemView.setOnClickListener(data.onClickListener)
                if (data.iconResId != -1) {
                    vh.mIcon.setImageResource(data.iconResId)
                    vh.mIcon.visibility = View.VISIBLE
                } else {
                    vh.mIcon.setImageDrawable(null)
                    vh.mIcon.visibility = View.GONE
                }
            }
            ROW_TYPE_TWO_LINE -> {
                val vh = holder as RowTextTwoLineViewHolder
                val data = objData as RowSettings
                vh.mTextOne.text = data.title
                vh.mTextTwo.text = data.subTitle
                vh.itemView.setOnClickListener(data.onClickListener)
                if (data.iconResId != -1) {
                    vh.mIcon.setImageResource(data.iconResId)
                    vh.mIcon.visibility = View.VISIBLE
                } else {
                    vh.mIcon.setImageDrawable(null)
                    vh.mIcon.visibility = View.GONE
                }
            }
            ROW_TYPE_DIV_INSET, ROW_TYPE_DIV -> {
                val vh = holder as RowDivViewHolder
                val data = objData as RowDiv
                if (data.isInset) {
                    vh.mSpacer.visibility = View.VISIBLE
                } else {
                    vh.mSpacer.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mRows.size
    }

    override fun getItemViewType(position: Int): Int {
        val data = mRows[position]
        return when (data) {
            is String -> ROW_TYPE_HEADER
            is RowSettings -> if (TextUtils.isEmpty(data.subTitle)) {
                ROW_TYPE_ONE_LINE
            } else {
                ROW_TYPE_TWO_LINE
            }
            is RowDiv -> if (data.isInset) {
                ROW_TYPE_DIV_INSET
            } else {
                ROW_TYPE_DIV
            }
            is RowSideBarHeading -> ROW_TYPE_SIDE_BAR_HEADING
            else -> super.getItemViewType(position)
        }
    }

    public open class RowSettings {
        var title: String? = null
            private set
        var subTitle: String? = null
            private set
        var onClickListener: View.OnClickListener? = null
            private set
        var iconResId = -1
            private set

        constructor(title: String, subTitle: String, onClickListener: View.OnClickListener) {
            this.title = title
            this.subTitle = subTitle
            this.onClickListener = onClickListener
        }

        constructor(title: String, subTitle: String, onClickListener: View.OnClickListener, iconResId: Int) {
            this.title = title
            this.subTitle = subTitle
            this.onClickListener = onClickListener
            this.iconResId = iconResId
        }
    }

    public open class RowDiv(val isInset: Boolean)

    public open class RowSideBarHeading(val iconResId: Int, val title: String, val subtitle: String?)
}