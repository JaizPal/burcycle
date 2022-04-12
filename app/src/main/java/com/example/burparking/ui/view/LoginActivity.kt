package com.example.burparking.ui.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.burparking.R
import com.example.burparking.databinding.ActivityLoginBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val GOOGLE_SIG_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        getWindow().statusBarColor = ContextCompat.getColor(this, R.color.verdeClaro)
        setContentView(binding.root)
        session()
        setup()
    }

    private fun session() {
        val prefs = getSharedPreferences("inicioSesion", Context.MODE_PRIVATE)
        val email = prefs?.getString("email", null)
        val photoURI = prefs?.getString("photoURI", null)
        if (email != null && photoURI != null) {
            binding.inicioLayout.visibility = View.INVISIBLE
            navegarPrincipal(email, photoURI.toString())
        }
    }

    private fun setup() {

        binding.registrarButton.setOnClickListener {
            if (comprobarCampos()) {
                var email = binding.emailEditText.text.toString().trim().lowercase()
                val password = binding.paswordEditText.text.toString().trim()
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(
                        email,
                        password
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                            mostrarSnackBar("Se ha enviado un email de confirmación a su correo")
                        } else {
                            showAlert()
                        }
                    }
            }
        }
        binding.accederButton.setOnClickListener {
            if (comprobarCampos()) {
                FirebaseAuth.getInstance().signOut()
                var email = binding.emailEditText.text.toString().trim().lowercase()
                val password = binding.paswordEditText.text.toString().trim()
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        email,
                        password
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            email = it.result.user?.email ?: ""
                            val photoURI = it.result.user?.photoUrl ?: ""
                            if(FirebaseAuth.getInstance().currentUser?.isEmailVerified!!) {
                                navegarPrincipal(email, photoURI.toString())
                            } else {
                                binding.tvConfirmarEmail.text = "Confirme la verificación del correo electrónico"
                            }
                            //AuthUI.getInstance().signOut(this)

                        } else {
                            showAlert()
                        }
                    }
            }

        }
        binding.googleButton.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIG_IN)
        }
    }

    private fun mostrarSnackBar(mensaje: String) {
        Snackbar.make(
            this.findViewById(android.R.id.content),
            mensaje,
            Snackbar.LENGTH_INDEFINITE
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIG_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val email = it.result.user?.email ?: ""
                                val photoURI = it.result.user?.photoUrl ?: ""
                                navegarPrincipal(email, photoURI.toString())
                            } else {
                                showAlert()
                            }
                        }
                }
            } catch (e: ApiException) {
                showAlert()
            }
        }
    }

    private fun comprobarCampos(): Boolean {
        var emailCompletado = false
        var passwordCompletado = false
        val emailLayout = binding.emailLayout
        val email = binding.emailEditText.text
        val passwordLayout = binding.passwordLayout
        val password = binding.paswordEditText.text

        if (email.isNullOrEmpty()) {
            emailLayout.error = "Campo obligatorio"

        } else if (!email.contains("@")) {
            emailLayout.error = "Correo no válido"
        } else {
            emailLayout.error = null
            emailCompletado = true
        }
        if (password.isNullOrEmpty()) {
            passwordLayout.error = "Campo obligatorio"
        } else if (password.length < 5) {
            passwordLayout.error = "La contraseña debe contener al menos 5 caracteres"
        } else {
            passwordLayout.error = null
            passwordCompletado = true
        }
        return emailCompletado && passwordCompletado
    }

    private fun navegarPrincipal(email: String, photoURI: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("photoUri", photoURI)
        }
        startActivity(intent)
        finish()

    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autentificando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}