package com.brocolisoftware.concejales_project.activities

import android.app.Notification
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brocolisoftware.concejales_project.R
import com.brocolisoftware.concejales_project.activities.DashboardActivity.Companion.currentUser
import com.brocolisoftware.concejales_project.activities.LatestMessageActivity.Companion.USER_KEY
import com.brocolisoftware.concejales_project.adapters.NewMessageAdapter
import com.brocolisoftware.concejales_project.adapters.Chat
import com.brocolisoftware.concejales_project.entities.Messages
import com.brocolisoftware.concejales_project.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    lateinit var user : User
    val adapter = GroupAdapter<GroupieViewHolder>()
    lateinit var Notification : Notification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview_chat)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar!!)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)
        user = intent.getParcelableExtra<User>(USER_KEY)
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
                        var currentUser = DashboardActivity.currentUser
                        if (currentUser != null){
                            adapter.add(Chat.ChatToItem(message.text, currentUser))
                        }
                        else{
                            traerCurrentUser()
                            currentUser = DashboardActivity.currentUser ?: return
                            adapter.add(Chat.ChatToItem(message.text, currentUser))
                        }
                    }
                    else{
                        adapter.add(Chat.ChatFromItem(message.text, user))
                    }



                }

                recyclerView.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun traerCurrentUser() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/Usuarios/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                DashboardActivity.currentUser = p0.getValue(User::class.java)

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

            recyclerView.scrollToPosition(adapter.itemCount -1)


            FirebaseDatabase.getInstance().getReference("/Notifications/$toId")
                .push().setValue(message).addOnSuccessListener {
                }


            et_message.text.clear()
            toRef.setValue(message)

        }.addOnFailureListener {
            Toast.makeText(
                this,
                "MENSAJE NO ENVIADO POR "+it.message.toString(),
                Toast.LENGTH_LONG
            ).show()
        }


        val refLatest = FirebaseDatabase.getInstance().getReference("/ultimosMensajes/$fromId").push()
        refLatest.setValue(message)

        val refToLatest = FirebaseDatabase.getInstance().getReference("/ultimosMensajes/$toId").push()
        refToLatest.setValue(message)

    }

}



