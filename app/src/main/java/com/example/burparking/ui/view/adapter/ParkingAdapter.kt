package com.example.burparking.ui.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.burparking.R
import com.example.burparking.domain.model.Parking

class ParkingAdapter(private val parkings: List<Parking>): RecyclerView.Adapter<ParkingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ParkingViewHolder(layoutInflater.inflate(R.layout.item_parking, parent, false))
    }

    override fun onBindViewHolder(holder: ParkingViewHolder, position: Int) {
        val item = parkings[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = parkings.size

}