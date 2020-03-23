package com.brocolisoftware.concejales_project.adapters

import com.brocolisoftware.concejales_project.R
import com.brocolisoftware.concejales_project.entities.Messages
import com.brocolisoftware.concejales_project.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_latest_message.view.*

class LatestMessages(val message: Messages) :  Item(){
    var partnerUser: User? = null
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val partnerId : String
        if(message.fromId == FirebaseAuth.getInstance().currentUser?.uid)
            partnerId = message.toId
        else
            partnerId = message.fromId

        val ref = FirebaseDatabase.getInstance().getReference("/Usuarios/$partnerId")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                partnerUser = p0.getValue(User::class.java)
                if( partnerUser != null){
                    viewHolder.itemView.username_latest_message.text = partnerUser?.nombre + " " + partnerUser?.apellido

                    val uri = partnerUser?.foto
                    val target = viewHolder.itemView.image_latest_message
                    if(!uri.equals(""))
                        Picasso.get().load(uri).resize(400,400).into(target)
                }

            }
        })

        viewHolder.itemView.latest_messages_message.text = message.text

    }

    override fun getLayout() = R.layout.item_latest_message

}