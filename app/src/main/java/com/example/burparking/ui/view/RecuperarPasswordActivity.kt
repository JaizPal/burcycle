package com.example.burparking.ui.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.burparking.R
import com.example.burparking.databinding.ActivityRecuperarPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class RecuperarPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecuperarPasswordBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        getWindow().statusBarColor = ContextCompat.getColor(this, R.color.verdeClaro)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setContentView(binding.root)

        /*
         * Listener del botón enviar.
         * Si se produce un error muestra una alerta
         * sino navega a la ventana anterior
         */
        binding.enviarButton.setOnClickListener {
            if (comprobarEmail()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(
                    binding.emailEditText.text?.trim().toString().lowercase()
                ).addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Se ha envíado un correo a su dirección",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }.addOnFailureListener {
                    showAlert(it.message.toString())
                }
            }
        }
    }

    /*
     * Comprueba que el email introducido es correcto
     * mostrando los errores pertinentes.
     */
    private fun comprobarEmail(): Boolean {
        val emailLayout = binding.emailLayout
        val email = binding.emailEditText.text?.trim()
        var emailCorrecto = false

        if (email.isNullOrEmpty()) {
            emailLayout.error = "Campo obligatorio"

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.error = "Correo no válido"
        } else {
            emailLayout.error = null
            emailCorrecto = true
        }

        return emailCorrecto
    }

    /*
     * Muestra una alerta con el mensaje mandado por parámetros
     */
    private fun showAlert(mensaje: String) {
        val mensajeError = when (mensaje) {
            "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Error de conexión"
            "There is no user record corresponding to this identifier. The user may have been deleted." -> "El usuario no existe"
            else -> mensaje

        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(mensajeError)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}