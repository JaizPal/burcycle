package com.example.burparking.ui.view.adapter

import android.transition.AutoTransition
import android.transition.ChangeBounds
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.burparking.R
import com.example.burparking.databinding.ItemParkingBinding
import com.example.burparking.domain.model.Parking

class ParkingViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemParkingBinding.bind(view)

    fun render(parking: Parking) {
        binding.tvCapacidadParking.text = "Capacidad: " + parking.capacidad.toString()
        binding.tvDistancia.text = "Distancia: " + parking.distancia?.toInt().toString() + " metros"
        binding.cardArrow.setOnClickListener { cardOnClick() }
    }

    private fun cardOnClick() {
        val cardParking = binding.cardParking
        val cardArrow = binding.cardArrow
        val layoutExpand = binding.layoutExpand
        binding.tvDireccion.text = "Dirección: "


        if (layoutExpand.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(cardParking, Fade())
            layoutExpand.visibility = View.GONE
            cardArrow.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
        } else {
            TransitionManager.beginDelayedTransition(cardParking, AutoTransition())
            layoutExpand.visibility = View.VISIBLE
            cardArrow.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
        }

    }
}