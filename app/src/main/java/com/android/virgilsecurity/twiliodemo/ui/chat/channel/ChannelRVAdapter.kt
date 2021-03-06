/*
 * Copyright (c) 2015-2018, Virgil Security, Inc.
 *
 * Lead Maintainer: Virgil Security Inc. <support@virgilsecurity.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     (1) Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *
 *     (2) Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *     (3) Neither the name of virgil nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.android.virgilsecurity.twiliodemo.ui.chat.channel

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.virgilsecurity.twiliodemo.R
import com.android.virgilsecurity.twiliodemo.data.local.UserManager
import com.android.virgilsecurity.twiliodemo.data.remote.virgil.VirgilHelper
import com.android.virgilsecurity.twiliodemo.util.Constants
import com.twilio.chat.Message
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_holder_me.*
import kotlinx.android.synthetic.main.layout_holder_you.*
import java.util.*

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    4/16/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

class ChannelRVAdapter internal constructor(private val virgilHelper: VirgilHelper,
                                            private val userManager: UserManager)
    : RecyclerView.Adapter<ChannelRVAdapter.HolderMessage>() {

    private var items: MutableList<Message> = mutableListOf()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HolderMessage {
        val viewHolder: HolderMessage
        val inflater = LayoutInflater.from(viewGroup.context)

        viewHolder = when (viewType) {
            MESSAGE_ME -> HolderMessage(inflater.inflate(R.layout.layout_holder_me,
                                                         viewGroup,
                                                         false),
                                        userManager,
                                        virgilHelper)
            MESSAGE_YOU -> HolderMessage(inflater.inflate(R.layout.layout_holder_you,
                                                          viewGroup,
                                                          false),
                                         userManager,
                                         virgilHelper)
            else -> HolderMessage(inflater.inflate(R.layout.layout_holder_me,
                                                   viewGroup,
                                                   false),
                                  userManager,
                                  virgilHelper)
        }

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: HolderMessage, position: Int) {
        viewHolder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        val attributes = items[position].attributes

        val sender = attributes[Constants.KEY_SENDER] as String
        val currentUser = userManager.getCurrentUser()!!.identity

        return if (sender == currentUser) {
            MESSAGE_ME
        } else {
            MESSAGE_YOU
        }
    }

    override fun getItemCount() = items.size

    fun setItems(items: MutableList<Message>?) {
        if (items != null) {
            items.removeAll(this.items)
            this.items = ArrayList(items)
        } else {
            this.items = mutableListOf()
        }

        notifyDataSetChanged()
    }

    fun addItems(channels: List<Message>) {
        items.addAll(channels)
        notifyDataSetChanged()
    }

    fun addItem(channel: Message) {
        items.add(channel)
        notifyDataSetChanged()
    }

    fun clearItems() {
        items.clear()
    }

    class HolderMessage(override val containerView: View?,
                        private val userManager: UserManager,
                        private val virgilHelper: VirgilHelper) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(message: Message) {
            val attributes = message.attributes

            val sender = attributes[Constants.KEY_SENDER] as String
            val currentUser = userManager.getCurrentUser()!!.identity

            if (currentUser == sender)
                tvMessageMe.text = virgilHelper.decrypt(message.messageBody)
            else
                tvMessageYou.text = virgilHelper.decrypt(message.messageBody)
        }
    }

    companion object {
        private const val MESSAGE_ME = 0
        private const val MESSAGE_YOU = 1
    }
}
