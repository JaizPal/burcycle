package com.example.burparking.ui.view.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.burparking.R
import com.example.burparking.databinding.ItemParkingBinding
import com.example.burparking.domain.model.Parking

class ParkingViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val binding = ItemParkingBinding.bind(view)

    fun render(parking: Parking) {
        binding.tvIdParking.text = parking.id.toString()
        binding.tvCapacidadParking.text = parking.capacidad.toString()
    }
}