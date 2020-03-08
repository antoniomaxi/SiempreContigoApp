package com.brocolisoftware.concejales_project.activities

import android.R.attr.name
import android.R.attr.password
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.brocolisoftware.concejales_project.R
import com.brocolisoftware.concejales_project.entities.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import kotlin.collections.HashMap


class SignUpActivity : AppCompatActivity() {

    private lateinit var ed_Nombre : EditText
    private lateinit var ed_Apellido : EditText
    private lateinit var ed_Cedula : EditText
    private lateinit var ed_Direccion : EditText
    private lateinit var ed_Phone : EditText
    private lateinit var ed_Email : EditText
    private lateinit var ed_Password1 : EditText
    private lateinit var ed_Password2 : EditText
    private lateinit var btn_Register : Button
    private var photo : Uri? = null

    //Firebase
    private lateinit var mAuth : FirebaseAuth;
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        inicializacionVista()
        inicializacionFirebase()

    }

    override fun onStart() {
        super.onStart()

        if(mAuth.currentUser != null){

        }
    }

    private fun registerUser(){
        if(ed_Nombre.text.toString().trim().isEmpty()){
            ed_Nombre.error = "Nombre Requerido"
            ed_Nombre.requestFocus()
            return
        }

        if(ed_Apellido.text.toString().trim().isEmpty()){
            ed_Apellido.error = "Apellido Requerido"
            ed_Apellido.requestFocus()
            return
        }

        if(ed_Cedula.text.toString().trim().isEmpty()){
            ed_Cedula.error = "Cedula Requerida"
            ed_Cedula.requestFocus()
            return
        }

        if(ed_Direccion.text.toString().trim().isEmpty()){
            ed_Direccion.error = "Direccion Requerida"
            ed_Direccion.requestFocus()
            return
        }

        if(ed_Phone.text.toString().trim().isEmpty()){
            ed_Phone.error = "Edad Requerida"
            ed_Phone.requestFocus()
            return
        }

        if(ed_Email.text.toString().trim().isEmpty()){
            ed_Email.error = "Email Requerido"
            ed_Email.requestFocus()
            return
        }

        if(ed_Password1.text.toString().trim().isEmpty()){
            ed_Password1.error = "Contraseña Requerida"
            ed_Password1.requestFocus()
            return
        }

        if(ed_Password2.text.toString().trim().isEmpty() || !ed_Password2.text.toString().trim().equals(ed_Password1.text.toString().trim())){
            ed_Password2.error = "Contraseñas no coinciden"
            ed_Password2.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(ed_Email.text.toString().trim()).matches()) {
            ed_Email.error = "Email con formato incorrecto"
            ed_Email.requestFocus();
            return;
        }

        val dialog: android.app.AlertDialog? = SpotsDialog.Builder()
            .setContext(this)
            .setCancelable(false)
            .setTheme(R.style.CustomAlert)
            .setMessage("Registrando...")
            .build()
            .apply {
                show()
            }

        mAuth.createUserWithEmailAndPassword(ed_Email.text.toString().trim(), ed_Password1.text.toString().trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    var uid = mAuth.uid
                    val user = User(
                        uid!!,
                        ed_Nombre.text.toString().trim(),
                        ed_Apellido.text.toString().trim(),
                        ed_Email.text.toString().trim(),
                        ed_Direccion.text.toString().trim(),
                        ed_Phone.text.toString().trim(),
                        ed_Cedula.text.toString().trim()
                    )

                    //subirFoto()

                    database = FirebaseDatabase.getInstance().getReference("/Usuarios/$uid")
                    database.setValue(user)
                        .addOnSuccessListener {
                            dialog!!.dismiss()
                            mAuth.signOut()
                            Toast.makeText(
                                this,
                                "Registro Exitoso",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                        .addOnFailureListener {
                            mAuth.signOut()
                            dialog!!.dismiss()
                            Toast.makeText(
                                this,
                                task.exception?.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                } else {
                    dialog!!.dismiss()
                    Toast.makeText(
                        this,
                        task.exception?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

    }

    private fun inicializacionFirebase() {
        mAuth = FirebaseAuth.getInstance()
        //database = FirebaseDatabase.getInstance().reference


    }

    private fun inicializacionVista() {
        ed_Nombre = findViewById<EditText>(R.id.Nombre)
        ed_Apellido = findViewById<EditText>(R.id.Apellido)
        ed_Cedula = findViewById<EditText>(R.id.Cedula)
        ed_Direccion = findViewById<EditText>(R.id.Direccion)
        ed_Phone = findViewById<EditText>(R.id.Phone)
        ed_Email = findViewById<EditText>(R.id.Email)
        ed_Password1 = findViewById<EditText>(R.id.Password1)
        ed_Password2 = findViewById<EditText>(R.id.Password2)
        btn_Register = findViewById<Button>(R.id.btn_sign_up)

        btn_Register.setOnClickListener {
            registerUser()
        }

        /*btn_select_photo.setOnClickListener {
            seleccionarFoto()
        }*/
    }

    private fun seleccionarFoto() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,0)
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            photo = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,photo)

            val bitmapDrawable = BitmapDrawable(this.resources, bitmap)
            btn_select_photo.background = bitmapDrawable

        }
    }*/

    /*fun subirFoto(){
        if(photo == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(photo!!)
    }*/
}
