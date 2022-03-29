package com.example.burparking.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.example.burparking.data.database.entities.ParkingEntity
import com.example.burparking.data.model.parking.ParkingModel

data class Parking(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val capacidad: Int,
    var numero: String?,
    var calle: String?,
    var distancia: Float?,
    var direccion: Direccion?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readParcelable(Direccion::class.java.classLoader)
    ) {
    }

    override fun toString(): String {
        return direccion.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeDouble(lat)
        parcel.writeDouble(lon)
        parcel.writeInt(capacidad)
        parcel.writeString(numero)
        parcel.writeString(calle)
        parcel.writeValue(distancia)
        parcel.writeParcelable(direccion, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Parking> {
        override fun createFromParcel(parcel: Parcel): Parking {
            return Parking(parcel)
        }

        override fun newArray(size: Int): Array<Parking?> {
            return arrayOfNulls(size)
        }
    }
}



fun ParkingModel.toDomain() = Parking(id, lat, lon, tags.capacidad, null, null, null, null)
fun ParkingEntity.toDomain() = Parking(id, lat, lon, capacidad, null, null, null, null)