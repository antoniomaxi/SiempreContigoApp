package com.brocolisoftware.concejales_project.entities

import java.sql.Timestamp

class Messages(val id:String,val text:String,
                val fromId:String,val toId:String,
                val timestamp: Long) {

constructor() : this("","","","",-1)
}
