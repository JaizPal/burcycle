package com.example.burparking.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.burparking.R
import com.example.burparking.databinding.FragmentReporteBinding
import com.example.burparking.domain.model.Parking
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import dagger.hilt.android.AndroidEntryPoint

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class ReporteFragment : Fragment() {

    private var _binding: FragmentReporteBinding? = null
    private val binding get() = _binding!!
    private lateinit var parking: Parking
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReporteBinding.inflate(inflater, container, false)
        parking = ReporteFragmentArgs.fromBundle(requireArguments()).parking
        binding.buttonAddIncidencia.setOnClickListener {
            irIncidencia()
        }

        binding.buttonEnviar.setOnClickListener {
            addReporte()
        }

        return binding.root
    }

    private fun irIncidencia() {
        val intent = Intent(activity, IncidenciaActivity::class.java).apply {
            putExtra("parking", parking)
        }
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun addReporte() {
        if(binding.capacidad.text.isNullOrEmpty()) {
            binding.incidenciaTextField.error = "Campo obligatorio"
        } else {
            binding.incidenciaTextField.error = null
            db.collection("reportes").document(
                SimpleDateFormat(
                    "dd-MM-yyyy HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())
            ).set(hashMapOf(
                "capacidad" to binding.capacidad.text.toString().toInt(),
                "fechaReporte" to Timestamp(Date()),
                "idParking" to parking.id
            ))
            Toast.makeText(requireContext(), "Gracias por el reporte", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_reporteFragment_to_principalFragment)
        }


    }


}