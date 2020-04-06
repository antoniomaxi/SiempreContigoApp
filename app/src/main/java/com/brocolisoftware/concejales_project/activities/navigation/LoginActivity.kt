package com.brocolisoftware.concejales_project.activities.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.brocolisoftware.concejales_project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dmax.dialog.SpotsDialog
import kotlin.system.exitProcess


class LoginActivity : AppCompatActivity() {

    private lateinit var ed_Email : EditText
    private lateinit var ed_Password : EditText
    private lateinit var tv_Register : TextView
    private lateinit var btn_Login : Button

    private lateinit var mAuth : FirebaseAuth;
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inicializarVista()
        inicializarFirebase()


    }

    private fun inicializarFirebase() {
        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
    }

    private fun inicializarVista() {
        ed_Email = findViewById<EditText>(R.id.email_login)
        ed_Password = findViewById<EditText>(R.id.password_login)
        btn_Login = findViewById<Button>(R.id.btn_Login)
        tv_Register = findViewById<Button>(R.id.go_Register)

        btn_Login.setOnClickListener {
            signIn()
        }
        tv_Register.setOnClickListener {
            signUp()
        }


    }

    public override fun onStart() {
        super.onStart()

        // Check auth on Activity start
        mAuth.currentUser?.let {
           onAuthSuccess(it)
        }
    }

    private fun onAuthSuccess(user: FirebaseUser) {

        // Go to MainActivity
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("user",user)
        startActivity(intent)
        finish()
    }


    private fun signIn() {

        if(ed_Email.text.toString().trim().isEmpty()){
            ed_Email.error = "Email Requerido"
            ed_Email.requestFocus()
            return
        }

        if(ed_Password.text.toString().trim().isEmpty()){
            ed_Password.error = "Contraseña Requerida"
            ed_Password.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(ed_Email.text.toString().trim()).matches()) {
            ed_Email.error = "Email con formato incorrecto"
            ed_Email.requestFocus();
            return;
        }

        val email = ed_Email.text.toString().trim()
        val password = ed_Password.text.toString().trim()

        val dialog: android.app.AlertDialog? = SpotsDialog.Builder()
            .setContext(this)
            .setCancelable(false)
            .setTheme(R.style.CustomAlert)
            .setMessage("Iniciando Sesión...")
            .build()
            .apply {
                show()
            }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    dialog!!.dismiss()
                    onAuthSuccess(task.result?.user!!)
                } else {
                    dialog!!.dismiss()
                    Toast.makeText(baseContext, task.exception?.message,
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun signUp(){

        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        exitProcess(0)

    }

}
