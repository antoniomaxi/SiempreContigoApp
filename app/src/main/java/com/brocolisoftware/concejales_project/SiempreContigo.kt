package com.brocolisoftware.concejales_project

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class SiempreContigo : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }


}