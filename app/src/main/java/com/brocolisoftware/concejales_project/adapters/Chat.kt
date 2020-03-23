package com.brocolisoftware.concejales_project.adapters

import com.brocolisoftware.concejales_project.R
import com.brocolisoftware.concejales_project.entities.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_messages_from.view.*
import kotlinx.android.synthetic.main.item_messages_to.view.*

class Chat {

    class ChatFromItem(val text: String, val user: User) :  Item(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            viewHolder.itemView.new_message_from.text = text
            val uri = user.foto
            val target = viewHolder.itemView.photo_from
            Picasso.get().load(uri).resize(400,400).into(target)

        }

        override fun getLayout() = R.layout.item_messages_from

    }

    class ChatToItem(val text: String, val user: User) :  Item(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            viewHolder.itemView.new_message_to.text = text
            val uri = user.foto
            val target = viewHolder.itemView.photo_to
            Picasso.get().load(uri).resize(400,400).into(target)

        }

        override fun getLayout() = R.layout.item_messages_to

    }

}