package com.brocolisoftware.concejales_project.entities

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(var uid: String,
           var nombre: String,
           var apellido: String,
           var email: String,
           var direccion: String,
           var telefono: String,
           var cedula: String,
           var Admin: Boolean = false,
           var foto: String) : Parcelable {

    constructor() : this("","","","","","","",false,"")

}