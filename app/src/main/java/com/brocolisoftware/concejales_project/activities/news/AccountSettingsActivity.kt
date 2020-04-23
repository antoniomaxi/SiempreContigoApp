package com.brocolisoftware.concejales_project.activities.news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.brocolisoftware.concejales_project.R
import kotlinx.android.synthetic.main.activity_account_settings.*

class AccountSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        inicializarListeners()
    }

    private fun inicializarListeners() {

        close_edit_profile.setOnClickListener {
            finish()
        }

        save_edit_profile.setOnClickListener {
            validarSalida()
            finish()
        }

    }

    private fun validarSalida() {

    }


}
