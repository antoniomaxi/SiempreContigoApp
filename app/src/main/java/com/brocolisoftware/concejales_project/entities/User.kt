package com.brocolisoftware.concejales_project.entities

import android.os.Parcelable
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
           var foto: String,
           var device_id : String) : Parcelable {

    constructor() : this("","","","","","","",false,"", "")

}