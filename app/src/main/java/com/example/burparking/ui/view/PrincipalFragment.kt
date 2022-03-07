package com.example.burparking.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.burparking.R
import com.example.burparking.databinding.FragmentPrincipalBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

        binding.autoCompleteDireccion.setOnClickListener{
            navegarBuscarDireccion()
        }

        return binding.root
    }

    private fun navegarBuscarDireccion() {
        findNavController().navigate(R.id.action_principalFragment_to_buscarDireccionFragment)
    }

}