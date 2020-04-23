package com.brocolisoftware.concejales_project.activities.chat

import android.app.Notification
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brocolisoftware.concejales_project.R
import com.brocolisoftware.concejales_project.activities.navigation.DashboardActivity
import com.brocolisoftware.concejales_project.adapters.ChatAdapter
import com.brocolisoftware.concejales_project.entities.Messages
import com.brocolisoftware.concejales_project.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    lateinit var user : User
    val adapter = GroupAdapter<GroupieViewHolder>()
    lateinit var Notification : Notification
    lateinit var progressBar : CircularProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview_chat)

        progressBar = findViewById<CircularProgressBar>(R.id.circularProgressBar)
        progressBar.visibility = View.VISIBLE


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar!!)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)
        if(intent == null ) return
        user = intent?.getParcelableExtra<User>("USER_KEY")!!
        if(user == null ) return
        supportActionBar?.title = user?.nombre + " " + user?.apellido

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter

        listenForMessages()

        inicializarListener()
    }

    private fun inicializarListener() {
        send_button.setOnClickListener {
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v.vibrate(50)
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
                progressBar.visibility = View.VISIBLE
                val message = p0.getValue(Messages::class.java)
                if(message != null) {

                    if (message.fromId == FirebaseAuth.getInstance().currentUser?.uid){
                        var currentUser = DashboardActivity.currentUser
                        if (currentUser != null){
                            adapter.add(ChatAdapter.ChatToItem(message.text, currentUser))
                        }
                        else{
                            traerCurrentUser()
                            currentUser = DashboardActivity.currentUser ?: return
                            adapter.add(ChatAdapter.ChatToItem(message.text, currentUser))
                        }
                    }
                    else{
                        adapter.add(ChatAdapter.ChatFromItem(message.text, user))
                    }

                }

                recyclerView.scrollToPosition(adapter.itemCount - 1)
                progressBar.visibility = View.GONE
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
        progressBar.visibility = View.GONE

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

        if(fromId == null || text.isEmpty()) return
        val ref = FirebaseDatabase.getInstance().getReference("/Mensajes/$fromId/$toId").push()
        ref.keepSynced(true)
        val toRef = FirebaseDatabase.getInstance().getReference("/Mensajes/$toId/$fromId").push()
        toRef.keepSynced(true)
        et_message.text.clear()
        val message = Messages(ref.key!!,text,fromId,toId,System.currentTimeMillis()/1000)
        ref.setValue(message).addOnSuccessListener {

            recyclerView.scrollToPosition(adapter.itemCount -1)


            FirebaseDatabase.getInstance().getReference("/Notifications/$toId")
                .push().setValue(message).addOnSuccessListener {
                }
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

    override fun onRestart() {
        super.onRestart()
        /*val i = Intent(this,LatestMessageActivity::class.java)
        startActivity(i)*/
        //progressBar.visibility = View.VISIBLE
        finish()
    }

}



