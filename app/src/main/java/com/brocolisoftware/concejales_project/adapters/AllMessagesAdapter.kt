package com.brocolisoftware.concejales_project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brocolisoftware.concejales_project.R
import com.brocolisoftware.concejales_project.entities.Messages
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.main.item_new_message.view.*
import java.util.ArrayList

class AllMessagesAdapter(val lista: ArrayList<Messages>) : RecyclerView.Adapter<AllMessagesAdapter.ViewHolder>() {

    private val layout = R.layout.item_messages_from
    private val layout2 = R.layout.item_messages_to

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllMessagesAdapter.ViewHolder {
        val context = parent!!.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        // Inflate the custom layout
        var item = inflater.inflate(layout, parent, false)



        // Return a new holder instance
        // Return a new holder instance
        return ViewHolder(item,lista)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(holder: AllMessagesAdapter.ViewHolder, position: Int) {
        if (lista[position] != null) {
            if (holder!!.tv_username != null) {
                holder.tv_username.text = "hahsahsjajsjajsjsajsjasjsajjsa"
                //Picasso.get().load(lista[position].foto).into(holder.image)
            }
        }
    }

    class ViewHolder(itemView: View, val lista: ArrayList<Messages>) : RecyclerView.ViewHolder(itemView){
        var tv_username = itemView.username_new_message as TextView
        var image = itemView.btn_select_photo as CircularImageView


        init {
            itemView.setOnClickListener {

            }
        }


    }


}