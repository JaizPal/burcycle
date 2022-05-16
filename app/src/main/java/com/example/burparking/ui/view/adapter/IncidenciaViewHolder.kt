package com.example.burparking.ui.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.burparking.databinding.ItemIncidenciaBinding
import com.example.burparking.domain.model.Incidencia
import java.text.SimpleDateFormat
import java.util.*

class IncidenciaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemIncidenciaBinding.bind(view)

    /*
     * Se encarga de pintar y definir el funcionamiento de cada item del
     * recyclerView
     */
    fun render(incidencia: Incidencia) {
        binding.tvFechaIncidencia.text = incidencia.fecha
        binding.tipoIncidencia.text = incidencia.tipo
        binding.tvDescripcionIncidencia.text = incidencia.descripcion
    }
}