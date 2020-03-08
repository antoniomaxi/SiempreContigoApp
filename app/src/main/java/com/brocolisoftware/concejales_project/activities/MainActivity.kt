package com.brocolisoftware.concejales_project.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.brocolisoftware.concejales_project.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val handler = Handler()

        // run a thread after 2 seconds to start the home screen
        handler.postDelayed({

            val intent = Intent(this,
                LoginActivity::class.java)
            startActivity(intent)
            finish()

        }, 2000)


    }


}
