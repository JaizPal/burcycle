package com.example.burparking.ui.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.burparking.R
import com.example.burparking.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val GOOGLE_SIG_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
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
            lifecycleScope.launch {
                navegarPrincipal(email, photoURI.toString(), 0L)
            }
        }
    }

    private fun setup() {

        binding.registrarButton.setOnClickListener {
            if (comprobarCampos()) {
                val email = binding.emailEditText.text.toString().trim().lowercase()
                val password = binding.paswordEditText.text.toString().trim()
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(
                        email,
                        password
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                            mostrarSnackBar("Se ha enviado un email de confirmación a su correo")
                        }
                    }.addOnFailureListener {
                        showAlert((it.message.toString()))
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
                            if (FirebaseAuth.getInstance().currentUser?.isEmailVerified!!) {
                                lifecycleScope.launch {
                                    navegarPrincipal(email, photoURI.toString(), 300L)
                                }
                            } else {
                                binding.tvConfirmarEmail.text =
                                    "Confirme la verificación del correo electrónico"
                                binding.tvConfirmarEmail.visibility = View.VISIBLE
                            }
                        }
                    }.addOnFailureListener {
                        showAlert(it.message.toString())
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

        binding.tvRecuperar.setOnClickListener {
            navegarRecuperarPassword()
        }
    }

    private fun mostrarSnackBar(mensaje: String) {
        Snackbar.make(
            this.findViewById(android.R.id.content),
            mensaje,
            Snackbar.LENGTH_LONG
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
                                lifecycleScope.launch {
                                    navegarPrincipal(email, photoURI.toString(), 300L)
                                }
                            }
                        }.addOnFailureListener {
                            showAlert(it.message.toString())
                        }
                }
            } catch (e: ApiException) {
                showAlert(e.message.toString())
            }
        }
    }

    private fun comprobarCampos(): Boolean {
        var emailCompletado = false
        var passwordCompletado = false
        val emailLayout = binding.emailLayout
        val email = binding.emailEditText.text?.trim()
        val passwordLayout = binding.passwordLayout
        val password = binding.paswordEditText.text

        if (email.isNullOrEmpty()) {
            emailLayout.error = "Campo obligatorio"

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

    private suspend fun navegarPrincipal(email: String, photoURI: String, delay: Long) {
        aplicarAnimaciones()
        delay(delay)
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("photoUri", photoURI)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun navegarRecuperarPassword() {
        val intent = Intent(this, RecuperarPasswordActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun showAlert(mensaje: String) {
        Log.i("Fail", mensaje)
        val mensajeError = when (mensaje) {
            "The password is invalid or the user does not have a password." -> "La contraseña es inválida o el usuario no tiene contraseña"
            "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Error de conexión"
            "There is no user record corresponding to this identifier. The user may have been deleted." -> "El usuario no existe"
            "12501: " -> "Inicio de sesión cancelado"
            else -> {
                mensaje
            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(mensajeError)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun aplicarAnimaciones() {
        binding.emailLayout.animate()
            .alpha(0f)
            .translationXBy(1200f)
            .duration = 400L
        binding.passwordLayout.animate()
            .alpha(0f)
            .translationXBy(-1200f)
            .duration = 400L
        binding.tvRecuperar.animate()
            .alpha(0F)
            .translationXBy(1200f)
            .duration = 400L
        binding.registrarButton.animate()
            .translationXBy(-1200f)
            .duration = 400L
        binding.accederButton.animate()
            .translationXBy(1200f)
            .duration = 400L
        binding.googleButton.animate()
            .alpha(0F)
            .duration = 400L
    }

}