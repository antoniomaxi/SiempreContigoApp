package com.brocolisoftware.concejales_project.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.brocolisoftware.concejales_project.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        inicializarListeneres()
    }

    private fun inicializarListeneres() {
        Emergencia.setOnClickListener {
            val intent = Intent(this, LatestMessageActivity::class.java)
            startActivity(intent)

        }
        Logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
        }
    }
}
