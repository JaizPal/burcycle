package com.example.burparking.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.example.burparking.data.database.entities.DireccionEntity
import com.example.burparking.data.model.direccion.DireccionModel

/*
 * Modelo de Direccion usado en la app
 * Extiende de Parceable para poder mandar una instancia
 * como parámetro de un Fragment
 */
data class Direccion(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val numero: String?,
    var calle: String?,
    val codigoPostal: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun toString(): String {
        return if (!calle.isNullOrEmpty()) {
            if (!numero.isNullOrEmpty()) {
                if (!codigoPostal.isNullOrEmpty()) {
                    calle!! + " " + numero + " " + codigoPostal
                } else {
                    calle!! + " " + numero
                }
            } else {
                calle!!
            }
        } else {
            " "
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeDouble(lat)
        parcel.writeDouble(lon)
        parcel.writeString(numero)
        parcel.writeString(calle)
        parcel.writeString(codigoPostal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Direccion> {
        override fun createFromParcel(parcel: Parcel): Direccion {
            return Direccion(parcel)
        }

        override fun newArray(size: Int): Array<Direccion?> {
            return arrayOfNulls(size)
        }
    }
}

fun DireccionModel.toDomain() = Direccion(id, lat, lon, tags.numero, tags.calle, tags.codigoPostal)
fun DireccionEntity.toDomain() = Direccion(id, lat, lon, numero, calle, codigoPostal)