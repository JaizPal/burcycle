package com.example.burparking

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.burparking.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val GOOGLE_SIG_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
        session()

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
            if(binding.emailEditText.text.isNotEmpty() && binding.paswordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(binding.emailEditText.text.toString(),
                        binding.paswordEditText.text.toString()).addOnCompleteListener{
                        if (it.isSuccessful) {
//                            navegarPrincipal(it.result.user?.email ?: "", PrincipalFragment.ProviderType.BASIC)
                            val email = it.result.user?.email ?: ""
                            val photoURI = it.result.user?.photoUrl ?: ""
                            navegarPrincipal(email, photoURI.toString())
                        } else {
                            showAlert()
                        }
                    }
            } else {
                toastIntroducirCampos()
            }
            Log.i("LOGG", "registrarButton")
        }
        binding.accederButton.setOnClickListener {
            if (binding.emailEditText.text.isNotEmpty() && binding.paswordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        binding.emailEditText.text.toString(),
                        binding.paswordEditText.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
//                            navegarPrincipal(
//                                it.result.user?.email ?: "",
//                                PrincipalFragment.ProviderType.BASIC
//                            )

                            val email = it.result.user?.email ?: ""
                            val photoURI = it.result.user?.photoUrl ?: ""
                            navegarPrincipal(email, photoURI.toString())
                        } else {
                            showAlert()
                        }
                    }
            } else {
                toastIntroducirCampos()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SIG_IN) {
            val task  = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{
                        if(it.isSuccessful) {
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

    private fun navegarPrincipal(email: String, photoURI: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("photoUri", photoURI)
        }
        startActivity(intent)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        finish()
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autentificando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }

    private fun toastIntroducirCampos() {
        Toast.makeText(this, "Introduce los campos", Toast.LENGTH_SHORT).show()
    }

}