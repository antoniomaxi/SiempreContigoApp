package com.brocolisoftware.concejales_project.activities.chat

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brocolisoftware.concejales_project.R
import com.brocolisoftware.concejales_project.adapters.LatestMessagesAdapter
import com.brocolisoftware.concejales_project.entities.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_latest_message.*

class LatestMessageActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    val adapter = GroupAdapter<GroupieViewHolder>()
    var latestMessagesMap : HashMap<String,Messages>? = null
    private lateinit var inAnimation : AlphaAnimation
    private lateinit var outAnimation : AlphaAnimation
    lateinit var progressBar : CircularProgressBar
    var messages : MutableList<Messages>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview_latest_message)
        progressBar = findViewById<CircularProgressBar>(R.id.circularProgressBar)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar!!)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)
        supportActionBar!!.title = "ULTIMOS MENSAJES"

        progressBar.visibility = View.VISIBLE

        messages = ArrayList()

        adapter.setOnItemClickListener { item, view ->

            val intent = Intent(this, ChatActivity::class.java)

            val fila = item as LatestMessagesAdapter

            intent.putExtra("USER_KEY",fila.partnerUser)
            if (fila.partnerUser != null){
                startActivity(intent)
                adapter.clear()
            }

        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            adapter.setOnItemLongClickListener { item, view ->
                deleteButton.visibility = View.VISIBLE

                view.background = getDrawable(R.drawable.background_onlong_click)



                true
            }
        }*/
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter

        latestMessagesMap = HashMap<String,Messages>()
        listenForLatestMessages()

    }


    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/ultimosMensajes/$fromId")



        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messages?.clear()

                for (snapshot in dataSnapshot.children){
                    val message = snapshot.getValue(Messages::class.java) ?: return

                    val partnerId : String
                    if(message.fromId == FirebaseAuth.getInstance().currentUser?.uid)
                        partnerId = message.toId
                    else
                        partnerId = message.fromId

                    latestMessagesMap!![partnerId] = message
                }
                refreshRecyclerView()
            }

        })

    }

    private fun refreshRecyclerView() {
        adapter.clear()
        adapter.notifyDataSetChanged()
        latestMessagesMap!!.values.forEach{
            adapter.add(
                LatestMessagesAdapter(
                    it
                )
            )
            }
        adapter.notifyDataSetChanged()
        progressBar.visibility = View.GONE

    }

    private fun refreshRecyclerView2() {
        adapter.clear()
        messages?.forEach{
            adapter.add(
                LatestMessagesAdapter(
                    it
                )
            )
        }
        //progressBar.visibility = View.GONE

    }

    override fun onRestart() {
        super.onRestart()
        adapter.clear()
        adapter.notifyDataSetChanged()
        progressBar.visibility = View.VISIBLE
        latestMessagesMap = HashMap<String,Messages>()
        listenForLatestMessages()
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
