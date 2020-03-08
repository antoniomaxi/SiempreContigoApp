package com.brocolisoftware.concejales_project.entities

import com.google.firebase.database.Exclude

class User {
    var uid: String
    var nombre: String
    var apellido: String
    var email: String
    var direccion: String
    var telefono: String
    var cedula: String
    var Admin: Boolean = false

    constructor(
        uid: String,
        Nombre: String,
        Apellido: String,
        Email: String,
        Direccion: String,
        Telefono: String,
        Cedula: String
    ) {
        this.uid = uid
        this.nombre = Nombre
        this.apellido = Apellido
        this.email = Email
        this.direccion = Direccion
        this.telefono = Telefono
        this.cedula = Cedula
        this.Admin = false
    }


}