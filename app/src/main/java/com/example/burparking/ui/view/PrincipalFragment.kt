package com.example.burparking.ui.view

import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.burparking.R
import com.example.burparking.databinding.FragmentPrincipalBinding
import com.example.burparking.ui.viewModel.PrincipalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrincipalFragment : Fragment() {

    private var _binding: FragmentPrincipalBinding? = null
    private val binding get() = _binding!!

    private val principalViewModel: PrincipalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrincipalBinding.inflate(inflater, container, false)

        /*
         * Llama al método del viewModel que descarga las frases de la BBDD
         */
        principalViewModel.onCreate()

        /*
         * Cuando el valor cambia, lo establece en la vista
         */
        principalViewModel.parte1.observe(requireActivity()) {
            binding.parte1.text = it
        }
        principalViewModel.parte2.observe(requireActivity()) {
            binding.parte2.text = it
        }
        principalViewModel.autor.observe(requireActivity()) {
            binding.autor.text = it
        }

        binding.buscarButton.setOnClickListener {
            navegarBuscarDireccion()
        }
        return binding.root
    }

    /*
     * Navega a la ventana de buscar dirección
     */
    private fun navegarBuscarDireccion() {
        aplicarAnimaciones()
        findNavController().navigate(R.id.action_principalFragment_to_buscarDireccionFragment)
    }

    /*
     * Define las animaciones que se ejecutarán cuando se llame al método
     */
    private fun aplicarAnimaciones() {
        binding.parte1.animate()
            .alpha(0f)
            .translationXBy(-1200f)
            .duration = 400L
        binding.parte2.animate()
            .alpha(0f)
            .translationXBy(-1200f)
            .duration = 400L
        binding.autor.animate()
            .alpha(0f)
            .translationXBy(1200f)
            .duration = 400L
        binding.buscarButton.animate()
            .alpha(0f)
            .duration = 400L
    }

}