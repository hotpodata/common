package com.hotpodata.common.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.hotpodata.common.R
import com.hotpodata.common.enums.Contacts
import timber.log.Timber
import java.util.*

/**
 * Created by jdrotos on 1/13/16.
 */
abstract open class Contact(val iconResId: Int, val name: String, val desc: String) {

    object Factory {
        fun genContacts(context: Context, appName: String): List<Contact> {
            var contacts = ArrayList<Contact>()
            for (contact in Contacts.values()) {
                contacts.add(createContact(context, contact, appName))
            }
            return contacts
        }

        fun createContact(context: Context, contact: Contacts, appName: String): Contact {
            var icon = when (contact) {
                Contacts.EMAIL -> R.drawable.ic_action_mail
                Contacts.GITHUB -> R.drawable.ic_action_github
                Contacts.TWITTER -> R.drawable.ic_action_twitter
                Contacts.WEB -> R.drawable.ic_action_web
            }
            var title = when (contact) {
                Contacts.EMAIL -> context.getString(R.string.email_title)
                Contacts.GITHUB -> context.getString(R.string.github_title)
                Contacts.TWITTER -> context.getString(R.string.twitter_title)
                Contacts.WEB -> context.getString(R.string.visit_website)

            }
            var blurb = when (contact) {
                Contacts.EMAIL -> context.getString(R.string.email_addr_TEMPLATE, appName)
                Contacts.GITHUB -> context.getString(R.string.github_handle)
                Contacts.TWITTER -> context.getString(R.string.twitter_handle)
                Contacts.WEB -> context.getString(R.string.visit_website_blurb)
            }

            var intentData = when (contact) {
                Contacts.EMAIL -> context.getString(R.string.email_addr_TEMPLATE, appName)
                Contacts.GITHUB -> context.getString(R.string.github_url)
                Contacts.TWITTER -> context.getString(R.string.twitter_url)
                Contacts.WEB -> context.getString(R.string.visit_website_url)
            }

            return when (contact) {
                Contacts.EMAIL -> object : Contact(icon, title, blurb) {
                    override fun genActionIntent(context: Context): Intent {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.setType("message/rfc822")
                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(intentData))
                        return intent
                    }
                }
                else -> object : Contact(icon, title, blurb) {
                    override fun genActionIntent(context: Context): Intent {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setData(Uri.parse(intentData))
                        return intent
                    }
                }
            }

        }
    }

    abstract fun genActionIntent(context: Context): Intent

    fun fireIntent(context: Context): Boolean {
        try {
            var intent = genActionIntent(context)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                return true
            }
        } catch(ex: Exception) {
            Timber.e(ex, "Failure to start contact intent")
        }
        return false
    }

}