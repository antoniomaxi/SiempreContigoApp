package com.brocolisoftware.concejales_project.activities

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brocolisoftware.concejales_project.R
import com.brocolisoftware.concejales_project.adapters.NewMessageAdapter
import com.brocolisoftware.concejales_project.entities.Messages
import com.brocolisoftware.concejales_project.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.item_messages_from.view.*
import kotlinx.android.synthetic.main.item_messages_to.view.*


class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    lateinit var user : User
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview_chat)

        user = intent.getParcelableExtra<User>(NewMessageAdapter.ViewHolder.USER_KEY)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar!!)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)
        supportActionBar?.title = user?.nombre + " " + user?.apellido

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter

        listenForMessages()

        send_button.setOnClickListener {
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v.vibrate(200)
            }
            sendMessages()
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().currentUser?.uid
        val toId = user.uid
        val ref = FirebaseDatabase.getInstance().getReference("/Mensajes/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(Messages::class.java)
                if(message != null) {

                    if (message.fromId == FirebaseAuth.getInstance().currentUser?.uid){
                        val currentUser = DashboardActivity.currentUser
                        adapter.add(ChatToItem(message.text,currentUser!!))

                    }
                    else
                        adapter.add(ChatFromItem(message.text,user))

                }

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

    private fun sendMessages() {
        val text = et_message.text.toString()
        val fromId = FirebaseAuth.getInstance().currentUser?.uid
        val toId = user.uid
        //val ref = FirebaseDatabase.getInstance().getReference("/Mensajes").push()
        val ref = FirebaseDatabase.getInstance().getReference("/Mensajes/$fromId/$toId").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/Mensajes/$toId/$fromId").push()

        if(fromId == null ) return
        val message = Messages(ref.key!!,text,fromId,toId,System.currentTimeMillis()/1000)
        ref.setValue(message).addOnSuccessListener {
            et_message.text.clear()
            recyclerView.scrollToPosition(adapter.itemCount -1)
        }

        toRef.setValue(message)

    }

    class ChatFromItem(val text: String, val user: User) :  Item(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            viewHolder.itemView.new_message_from.text = text
            val uri = user.foto
            val target = viewHolder.itemView.photo_from
            Picasso.get().load(uri).into(target)

        }

        override fun getLayout() = R.layout.item_messages_from

    }

    class ChatToItem(val text: String, val user: User) :  Item(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            viewHolder.itemView.new_message_to.text = text
            val uri = user.foto
            val target = viewHolder.itemView.photo_to
            Picasso.get().load(uri).into(target)

        }

        override fun getLayout() = R.layout.item_messages_to

    }
}



