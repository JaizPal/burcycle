package com.example.burparking.ui.view.adapter

import android.transition.AutoTransition
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.burparking.R
import com.example.burparking.databinding.ItemParkingBinding
import com.example.burparking.domain.model.Direccion
import com.example.burparking.domain.model.Parking
import com.example.burparking.ui.view.BuscarDireccionFragmentDirections


class ParkingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemParkingBinding.bind(view)

    /*
     * Se encarga de pintar y definir el funcionamiento de cada item del
     * recyclerView
     */
    fun render(parking: Parking, direccionActual: Direccion) {
        binding.tvCapacidadParking.text = "Capacidad: " + parking.capacidad.toString()
        binding.tvDistancia.text = "Distancia: " + parking.distancia?.toInt().toString() + " metros"
        binding.tvDireccion.text =
            (if (!parking.direccion?.calle.isNullOrEmpty()) {
                if (!parking.direccion?.numero.isNullOrEmpty()) {
                    parking.direccion?.calle + " " + parking.direccion?.numero
                } else {
                    parking.direccion?.calle
                }
            } else {
                " "
            }).toString()
        binding.cardParking.setOnClickListener { cardOnClick() }
        binding.cardArrow.setOnClickListener { cardOnClick() }
        binding.mapaButton.setOnClickListener {
            itemView.findNavController().navigate(
                BuscarDireccionFragmentDirections.actionBuscarDireccionFragmentToMapFragment(
                    direccionActual,
                    arrayOf(parking)
                )
            )
        }

        binding.irButton.setOnClickListener {
            itemView.findNavController().navigate(
                BuscarDireccionFragmentDirections.actionBuscarDireccionFragmentToInformacionFragment(
                    parking
                )
            )
        }

    }

    /*
     * Encargado de desplegar o contraer el item mediante una animación
     */
    private fun cardOnClick() {
        val cardParking = binding.cardParking
        val cardArrow = binding.cardArrow
        val layoutExpand = binding.layoutExpand

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