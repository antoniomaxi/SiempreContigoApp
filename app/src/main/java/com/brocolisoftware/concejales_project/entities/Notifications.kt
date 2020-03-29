package com.brocolisoftware.concejales_project.entities


class Notifications( var nombre: String,
                     var apellido: String,
                     val text:String,
                     val timestamp: Long) {

    constructor() : this("","","",-1)
}