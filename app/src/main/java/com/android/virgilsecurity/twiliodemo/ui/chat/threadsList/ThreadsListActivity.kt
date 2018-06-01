package com.android.virgilsecurity.twiliodemo.ui.chat.threadsList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import com.android.virgilsecurity.twiliodemo.R
import com.android.virgilsecurity.twiliodemo.data.local.UserManager
import com.android.virgilsecurity.twiliodemo.ui.base.BaseActivity
import com.android.virgilsecurity.twiliodemo.ui.chat.threadsList.dialog.CreateThreadDialog
import com.android.virgilsecurity.twiliodemo.ui.login.LoginActivity
import com.android.virgilsecurity.twiliodemo.util.UiUtils
import kotlinx.android.synthetic.main.activity_threads_list.*
import org.koin.android.ext.android.inject

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/29/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

class ThreadsListActivity : BaseActivity() {

    private val threadsListTag = "threadsListTag"
    private val userManager: UserManager by inject()
    private lateinit var createThreadDialog: CreateThreadDialog

    override fun provideLayoutId() = R.layout.activity_threads_list

    companion object {
        fun startWithFinish(from: Activity) {
            from.startActivity(Intent(from, ThreadsListActivity::class.java))
            from.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UiUtils.replaceFragmentNoBackStack(fragmentManager,
                                           flBaseContainer.id,
                                           ThreadsListFragment.newInstance(),
                                           threadsListTag)
    }

    private fun initDrawer() {
        val tvUsernameDrawer = nvNavigation.getHeaderView(0)
                .findViewById<TextView>(R.id.tvUsernameDrawer)
        tvUsernameDrawer.text = userManager.getCurrentUser()!!.identity

        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeButtonEnabled(true)

        nvNavigation.setNavigationItemSelectedListener(
            { drawerItem ->
                when (drawerItem.itemId) {
                    R.id.itemNewChat -> {
                        dlDrawer.closeDrawer(Gravity.START)
                        createThreadDialog = CreateThreadDialog(this,
                                                                R.style.NotTransBtnsDialogTheme,
                                                                getString(R.string.create_thread),
                                                                getString(R.string.enter_username))

                        createThreadDialog.setOnClickListener(
                            { dialog, identity ->
                                if (userManager.getCurrentUser()!!.identity == identity) {
                                    UiUtils.toast(this, R.string.no_chat_with_yourself)
                                } else {
                                    createThreadDialog.showProgress(true)
                                    val threadsListFragment =
                                            fragmentManager.findFragmentByTag(threadsListTag) as ThreadsListFragment
                                    threadsListFragment.issueCreateThread(identity)
                                }
                            },
                            {
                                (it as CreateThreadDialog).showProgress(false)
                            })

                        createThreadDialog.show()

                        return@setNavigationItemSelectedListener true
                    }
                    R.id.itemLogOut -> {
                        dlDrawer.closeDrawer(Gravity.START)

                        userManager.clearCurrentUser()
                        userManager.clearUserCard()

                        LoginActivity.startClearTop(this)
                        return@setNavigationItemSelectedListener true
                    }
                    else -> return@setNavigationItemSelectedListener false
                }
            })
    }
}
