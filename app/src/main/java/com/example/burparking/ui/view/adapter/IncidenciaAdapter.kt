package com.example.burparking.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.burparking.R
import com.example.burparking.domain.model.Incidencia

class IncidenciaAdapter(val incidencias: List<Incidencia>): RecyclerView.Adapter<IncidenciaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidenciaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return IncidenciaViewHolder(layoutInflater.inflate(R.layout.item_incidencia, parent, false))
    }

    override fun onBindViewHolder(holder: IncidenciaViewHolder, position: Int) {
        val item = incidencias[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = incidencias.size
}