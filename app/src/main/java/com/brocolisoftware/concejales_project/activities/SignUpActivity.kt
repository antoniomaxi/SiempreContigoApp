package com.brocolisoftware.concejales_project.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.security.AccessController.getContext
import java.util.*


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
    private lateinit var mAuth : FirebaseAuth
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

        if(photo == null){
            Toast.makeText(
                this,
                "Por Favor Seleccione una Imagen",
                Toast.LENGTH_LONG
            ).show()
            return
        }

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
            ed_Email.requestFocus()
            return
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

                    subirFoto(dialog,task)

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

        btn_select_photo.setOnClickListener {
            seleccionarFoto()
        }
    }

    private fun seleccionarFoto() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            photo = CropImage.getActivityResult(data).uri
            loadImage(photo)
        }
    }

    private fun loadImage(uriPhoto: Uri?) {
        Picasso.get().load(uriPhoto).into(btn_select_photo)
    }

    fun subirFoto(dialog: AlertDialog?, task: Task<AuthResult>) {
        if(photo == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/userPhotos/$filename")

         val bmp : Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photo)
                    val baos  =  ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos)
                    val data = baos.toByteArray()

        ref.putBytes(data)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                     salvarUsuarioEnFirebase(it.toString(),dialog,task)
                }
            }
            .addOnFailureListener {
                mAuth.signOut()
                dialog!!.dismiss()
                Toast.makeText(
                    this,
                    "Usuario NO Registrado",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun salvarUsuarioEnFirebase(photoUrl: String, dialog: AlertDialog?, task: Task<AuthResult>) {

        val deviceToken = ""

        val uid = mAuth.uid
        val user = User(
            uid!!,
            ed_Nombre.text.toString().trim(),
            ed_Apellido.text.toString().trim(),
            ed_Email.text.toString().trim(),
            ed_Direccion.text.toString().trim(),
            ed_Phone.text.toString().trim(),
            ed_Cedula.text.toString().trim(),
            false,
            photoUrl,
            deviceToken
        )

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

    }

}


