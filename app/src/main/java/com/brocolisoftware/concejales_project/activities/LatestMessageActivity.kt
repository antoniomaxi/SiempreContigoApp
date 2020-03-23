package com.brocolisoftware.concejales_project.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brocolisoftware.concejales_project.R
import com.brocolisoftware.concejales_project.adapters.LatestMessages
import com.brocolisoftware.concejales_project.entities.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class LatestMessageActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    val adapter = GroupAdapter<GroupieViewHolder>()
    val latestMessagesMap = HashMap<String,Messages>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview_latest_message)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar!!)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)
        supportActionBar!!.title = "ULTIMOS MENSAJES"

        adapter.setOnItemClickListener { item, view ->

            val intent = Intent(this, ChatActivity::class.java)

            val fila = item as LatestMessages

            intent.putExtra(USER_KEY,fila.partnerUser)
            if(USER_KEY != null)
                startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter

        listenForLatestMessages()


    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/ultimosMensajes/$fromId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(Messages::class.java)?: return

                val partnerId : String
                if(message.fromId == FirebaseAuth.getInstance().currentUser?.uid)
                    partnerId = message.toId
                else
                    partnerId = message.fromId

                latestMessagesMap[partnerId] = message
                refreshRecyclerView()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(Messages::class.java) ?: return
                val partnerId : String
                if(message.fromId == FirebaseAuth.getInstance().currentUser?.uid)
                    partnerId = message.toId
                else
                    partnerId = message.fromId

                latestMessagesMap[partnerId] = message
                refreshRecyclerView()
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun refreshRecyclerView() {
        adapter.clear()
        latestMessagesMap.values.forEach{
            adapter.add(
                LatestMessages(
                    it
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_latest_message,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            android.R.id.home -> {
                finish()
            }

        }

        return super.onOptionsItemSelected(item)
    }

}
