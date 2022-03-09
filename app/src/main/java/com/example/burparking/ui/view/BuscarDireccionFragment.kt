package com.example.burparking.ui.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.burparking.R
import com.example.burparking.databinding.FragmentBuscarDireccionBinding
import com.example.burparking.domain.model.Direccion
import com.example.burparking.domain.model.Parking
import com.example.burparking.ui.view.adapter.ParkingAdapter
import com.example.burparking.ui.viewModel.BuscarDireccionViewModel
import dagger.hilt.android.AndroidEntryPoint

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
    ): View?{
        _binding = FragmentBuscarDireccionBinding.inflate(inflater, container, false)

        val autoCompletado = binding.autoCompleteDirecciones
//        autoCompletado.requestFocus()
        if (autoCompletado.requestFocus())  {
            val imm: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(autoCompletado, InputMethodManager.SHOW_FORCED)
            Log.i("Algo", "Focusado")
        }
        buscarDireccionViewModel.isLoading.observe(requireActivity(), Observer {
            binding.progressBar.isVisible = it
        })

        buscarDireccionViewModel.direcciones.observe(requireActivity(), Observer {
            if (it != null) {
                adapterDirecciones = ArrayAdapter<Direccion>(
                    requireActivity(),
                    android.R.layout.simple_dropdown_item_1line,
                    buscarDireccionViewModel.direcciones.value!!.toList()
                )
                autoCompletado.setAdapter(adapterDirecciones)
            }
        })

        autoCompletado.setOnItemClickListener { parent, view, position, id ->
            buscarDireccionViewModel.buscarParkings(adapterDirecciones.getItem(position)!!)
        }

        buscarDireccionViewModel.closestParkings.observe(requireActivity(), Observer {
            if (it != null) {
                binding.tvUbicacionActual.text = it.size.toString()
                cargarCardsParkings()
            }
        })

        return binding.root
    }

    fun cargarCardsParkings() {
        val recyclerView = requireActivity().findViewById<RecyclerView>(R.id.recyclerViewParking)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ParkingAdapter(buscarDireccionViewModel.closestParkings.value!!)
    }

}