package com.example.burparking.data.model.reversedireccion

import com.google.gson.annotations.SerializedName

    data class ReverseDireccionModel(
        @SerializedName("osm_id")
        val id: Long,
        @SerializedName("name")
        val name: String,
        @SerializedName("housenumber")
        val numero: String?,
        @SerializedName("street")
        val calle: String?,
        @SerializedName("postcode")
        val codigoPostal: String?
    ) {
        override fun toString(): String {
        return (calle ?: "") + " " + (numero ?: "") + " " + (codigoPostal ?: "")
    }}

