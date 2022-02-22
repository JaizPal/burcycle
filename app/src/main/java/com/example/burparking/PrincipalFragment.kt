package com.example.burparking

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.burparking.databinding.FragmentPrincipalBinding
import com.google.firebase.auth.FirebaseAuth

class PrincipalFragment : Fragment() {

    enum class ProviderType {
        BASIC,
        GOOGLE
    }

    var _binding : FragmentPrincipalBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrincipalBinding.inflate(inflater, container, false)
        guardarInicioSesionUsuario()
        return binding.root
    }


    private fun guardarInicioSesionUsuario() {
        val argumentos : PrincipalFragmentArgs by navArgs()
        val email = argumentos.email
        val provider = argumentos.provider

        val prefs = activity?.getSharedPreferences("inicioSesion", Context.MODE_PRIVATE)!!.edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }

}