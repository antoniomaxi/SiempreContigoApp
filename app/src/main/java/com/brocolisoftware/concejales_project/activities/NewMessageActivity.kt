package com.brocolisoftware.concejales_project.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brocolisoftware.concejales_project.R
import com.brocolisoftware.concejales_project.adapters.NewMessageAdapter
import com.brocolisoftware.concejales_project.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_new_message.*
import java.util.ArrayList

class NewMessageActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    val lista: ArrayList<User> = ArrayList<User>()
    val context = this
    var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar!!)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)
        supportActionBar!!.title = "SELECCIONA UN USUARIO"

        recyclerView = findViewById<RecyclerView>(R.id.recyclerview_new_message)

        decidirSiEsAdmin()
        traerUsuarios()

    }

    private fun decidirSiEsAdmin() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/Usuarios/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)

                isAdmin = user!!.Admin
            }

        })
    }

    private fun traerUsuarios() {

        val dialog: android.app.AlertDialog? = SpotsDialog.Builder()
            .setContext(this)
            .setCancelable(false)
            .setTheme(R.style.CustomAlert)
            .setMessage("Por Favor Espere...")
            .build()
            .apply {
                show()
            }

        val ref = FirebaseDatabase.getInstance().getReference("/Usuarios")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {


                    val user = it.getValue(User::class.java)
                    if(isAdmin){
                        if(FirebaseAuth.getInstance().currentUser!!.uid != user!!.uid)
                            lista.add(user!!)
                    }else{
                        if(FirebaseAuth.getInstance().uid != user!!.uid &&  user.Admin)
                            lista.add(user!!)
                    }

                    Log.e("New Message",it.toString())
                }
                val obj_adapter = NewMessageAdapter(lista)

                recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                recyclerView.adapter = obj_adapter
            }
        })

        dialog!!.dismiss()
    }



}
