package com.brocolisoftware.concejales_project.activities.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.brocolisoftware.concejales_project.R
import com.brocolisoftware.concejales_project.activities.chat.LatestMessageActivity
import com.brocolisoftware.concejales_project.activities.complaints.UserComplaintActivity
import com.brocolisoftware.concejales_project.activities.news.NewsActivity
import com.brocolisoftware.concejales_project.entities.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    companion object{
        var currentUser: User? = null
    }

    lateinit var progressBar : CircularProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        progressBar = findViewById<CircularProgressBar>(R.id.circularProgressBar)
        progressBar.visibility = View.VISIBLE

        verificarLogeo()

        traerCurrentUser()
    }

     private fun traerCurrentUser() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/Usuarios/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                 currentUser = p0.getValue(User::class.java)
            }

        })
    }

    private fun verificarLogeo() {
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                    FirebaseDatabase.getInstance().getReference("/Usuarios/$uid/device_id")
                        .setValue(token).addOnSuccessListener {
                            progressBar.visibility = View.GONE
                            antispam.visibility = View.GONE
                            Toast.makeText(baseContext, "Bienvenido", Toast.LENGTH_SHORT).show()
                            inicializarListeneres()
                        }

                })


        }

    }

    private fun inicializarListeneres() {
        News.setOnClickListener {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }
        Emergencia.setOnClickListener {
            val intent = Intent(this, LatestMessageActivity::class.java)
            startActivity(intent)

        }
        Denuncia.setOnClickListener {
            val intent : Intent = if(currentUser!!.Concejal)
                Intent(this, UserComplaintActivity::class.java)
            else
                Intent(this, UserComplaintActivity::class.java)

            startActivity(intent)
        }
        Logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
