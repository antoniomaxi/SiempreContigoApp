package com.brocolisoftware.concejales_project.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brocolisoftware.concejales_project.R
import com.brocolisoftware.concejales_project.activities.chat.ChatActivity
import com.brocolisoftware.concejales_project.entities.User
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_new_message.view.*
import java.util.*


class NewMessageAdapter (val lista: ArrayList<User>) : RecyclerView.Adapter<NewMessageAdapter.ViewHolder>() {

    private val layout = R.layout.item_new_message

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent!!.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        // Inflate the custom layout
        val item = inflater.inflate(layout, parent, false)

        // Return a new holder instance
        // Return a new holder instance
        return ViewHolder(item,lista)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (lista[position] != null) {
            if (holder!!.tv_username != null) {
                holder.tv_username.text = lista[position].nombre.toUpperCase() + " " + lista[position].apellido.toUpperCase()

                Picasso.get().load(lista[position].foto).resize(400,400).into(holder.image)
            }
        }
    }

    class ViewHolder(itemView: View, val lista: ArrayList<User>) : RecyclerView.ViewHolder(itemView){
        var tv_username = itemView.username_new_message as TextView
        var image = itemView.btn_select_photo as CircularImageView



        init {
            itemView.setOnClickListener {

                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("USER_KEY",lista[adapterPosition])
                itemView.context.startActivity(intent)
                (itemView.context as Activity).finish()
            }
        }


    }
}