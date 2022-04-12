package com.example.burparking.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.burparking.databinding.FragmentInformacionBinding
import com.example.burparking.domain.model.Parking
import com.example.burparking.ui.view.adapter.IncidenciaAdapter
import com.example.burparking.ui.viewModel.InformacionViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class InformacionFragment : Fragment() {


    private var _binding: FragmentInformacionBinding? = null
    private val binding get() = _binding!!
    private lateinit var parking: Parking

    private val informacionViewModel: InformacionViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInformacionBinding.inflate(inflater, container, false)
        parking = InformacionFragmentArgs.fromBundle(requireArguments()).parking
        informacionViewModel.parking = parking
        binding.tvDireccion.text = parking.direccion.toString()
        binding.tvCapacidad.text = parking.capacidad.toString()
        informacionViewModel.onCreate()
        informacionViewModel.reportesRaw.observe(requireActivity()) {
            Log.i("Reportes", it.toString())
            Log.i("Reportes", "ID parking = " + parking.id)
            Log.i("Reportes", "Capacidad último reporte = " + it[0]["capacidad"].toString().toInt())
            Log.i(
                "Reportes",
                "Fecha último reporte = " + (it[0]["fechaReporte"] as Timestamp).toDate().toString()
            )

        }
        informacionViewModel.fechaUltimoReporte.observe(requireActivity()) {
            binding.tvFechaReporte.text = (if (it != null) {
                SimpleDateFormat(
                    "dd-MM-yyyy HH:mm:ss",
                    Locale.getDefault()
                ).format(it)
            } else {
                "No hay datos"
            }).toString()
        }

        informacionViewModel.capacidadUltimoReporte.observe(requireActivity()) {
            binding.tvUltimaCapacidad.text = (it ?: "").toString()
        }

        informacionViewModel.capacidadMedia.observe(requireActivity()) {
             if (!it.isNaN()) {
                 binding.tvCapacidadMedia.text = it.toString()
             } else {
                 binding.tvCapacidadMedia.text = "No hay datos"
             }
        }

        informacionViewModel.incidenciasCargadas.observe(requireActivity()) {
            if(it == true) {
                val recyclerView = binding.reciclerViewIncidencias
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = IncidenciaAdapter(informacionViewModel.incidencias.value!!.toList())
            }
        }
        return binding.root


    }

}