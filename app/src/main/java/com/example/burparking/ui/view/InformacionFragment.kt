package com.example.burparking.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.burparking.R
import com.example.burparking.databinding.FragmentInformacionBinding
import com.example.burparking.domain.model.Parking
import com.example.burparking.ui.view.adapter.IncidenciaAdapter
import com.example.burparking.ui.viewModel.InformacionViewModel
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
        setHasOptionsMenu(true)
        parking = InformacionFragmentArgs.fromBundle(requireArguments()).parking
        informacionViewModel.parking = parking
        binding.tvDireccion.text = parking.direccion.toString()
        binding.tvCapacidad.text = parking.capacidad.toString()
        informacionViewModel.onCreate()

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
            binding.tvCapacidadMedia.text = it.toString()
        }

        informacionViewModel.incidenciasCargadas.observe(requireActivity()) {
            if (it == true) {
                val recyclerView = binding.reciclerViewIncidencias
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter =
                    IncidenciaAdapter(informacionViewModel.incidencias.value!!.toList())
            }
        }

        binding.buttonIr.setOnClickListener {
            findNavController().navigate(
                InformacionFragmentDirections.actionInformacionFragmentToReporteFragment(
                    parking
                )
            )
        }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.shareButton) {
            compartirAparcamiento()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun compartirAparcamiento() {
        val uri = "http://maps.google.com/maps?q=loc:" + parking.lat + "," + parking.lon + " (" + "Aparcamiento bicicletas - Capacidad: ${parking.capacidad}" + ")"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(Intent.createChooser(intent, null))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

}