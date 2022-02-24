package com.example.burparking

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.burparking.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginFragment : Fragment() {

    var _binding : FragmentLoginBinding? = null
    val binding get() = _binding!!

    private val GOOGLE_SIG_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        setup()
        session()
        return binding.root
    }

    private fun session() {
        val prefs = activity?.getSharedPreferences("inicioSesion", Context.MODE_PRIVATE)
        val email = prefs?.getString("email", null)
        val provider = prefs?.getString("provider", null)
        if (email != null && provider != null) {
            binding.inicioLayout.visibility = View.INVISIBLE
            navegarPrincipal(email, PrincipalFragment.ProviderType.valueOf(provider))
        }
    }

    override fun onStart() {
        super.onStart()
        binding.inicioLayout.visibility = View.VISIBLE
    }

    private fun setup() {
        binding.registrarButton.setOnClickListener {
            if(binding.emailEditText.text.isNotEmpty() && binding.paswordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(binding.emailEditText.text.toString(),
                    binding.paswordEditText.text.toString()).addOnCompleteListener{
                        if (it.isSuccessful) {
                            navegarPrincipal(it.result.user?.email ?: "", PrincipalFragment.ProviderType.BASIC)
                        } else {
                            showAlert()
                        }
                    }
            } else {
                toastIntroducirCampos()
            }
        }
        binding.accederButton.setOnClickListener {
            if (binding.emailEditText.text.isNotEmpty() && binding.paswordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        binding.emailEditText.text.toString(),
                        binding.paswordEditText.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            navegarPrincipal(
                                it.result.user?.email ?: "",
                                PrincipalFragment.ProviderType.BASIC
                            )
                            val metadata = it.result.user?.metadata

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
            val googleClient = GoogleSignIn.getClient(requireActivity(), googleConf)
            googleClient.signOut()
            activity?.startActivityFromFragment(this, googleClient.signInIntent, GOOGLE_SIG_IN)
        }
    }

    private fun toastIntroducirCampos() {
        Toast.makeText(requireContext(), "Introduce los campos", Toast.LENGTH_SHORT).show()
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
                            navegarPrincipal(account.email ?: "", PrincipalFragment.ProviderType.GOOGLE)
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

    private fun showAlert() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autentificando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }

    private fun navegarPrincipal(email: String, provider: PrincipalFragment.ProviderType) {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToPrincipalFragment(email,
            provider.toString()
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}