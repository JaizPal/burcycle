package com.example.burparking.ui.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.burparking.R
import com.example.burparking.databinding.FragmentBuscarDireccionBinding
import com.example.burparking.domain.model.Direccion
import com.example.burparking.ui.viewModel.BuscarDireccionViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@AndroidEntryPoint
class BuscarDireccionFragment : Fragment() {

    private var _binding: FragmentBuscarDireccionBinding? = null
    private val binding get() = _binding!!

    private val buscarDireccionViewModel: BuscarDireccionViewModel by viewModels()
    private lateinit var adapterDirecciones: ArrayAdapter<Direccion>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buscarDireccionViewModel.onCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBuscarDireccionBinding.inflate(inflater, container, false)

        val autoCompletado = binding.autoCompleteDirecciones

        buscarDireccionViewModel.isLoading.observe(requireActivity(), Observer {
            binding.progressBar.isVisible = it
        })

        buscarDireccionViewModel.direcciones.observe(requireActivity(), Observer {
            if (it != null) {
                adapterDirecciones = ArrayAdapter<Direccion>(requireActivity(), android.R.layout.simple_dropdown_item_1line, buscarDireccionViewModel.direcciones.value!!.toList())
                autoCompletado.setAdapter(adapterDirecciones)
            }
        })




        return binding.root
    }

}