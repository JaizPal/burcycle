package com.example.burparking.ui.viewModel

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burparking.data.model.reversedireccion.ReverseDireccionModel
import com.example.burparking.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuscarDireccionViewModel @Inject constructor(
    private val getDireccionesUserCase: GetAllDireccionesUseCase,
    private val getClosestParkingsUseCase: GetClosestParkingsUseCase,
    private val getAllParkingsUseCase: GetAllParkingsUseCase,
    private val getReverseDireccionUseCase: GetReverseDireccionUseCase
) :
    ViewModel() {

    val direcciones = MutableLiveData<List<Direccion>>()
    val closestParkings = MutableLiveData<List<Parking>>()
    val parkings = MutableLiveData<List<Parking>>()
    val isLoading = MutableLiveData<Boolean>()
    var parkingCargados = MutableLiveData<Boolean>()
    var reverseDireccion = MutableLiveData<ReverseDireccionModel>()

    fun onCreate() {
        parkingCargados.postValue(false)
        viewModelScope.launch {
            isLoading.value = true
            direcciones.value = getDireccionesUserCase()!!
            isLoading.value = false
        }
        viewModelScope.launch {
            isLoading.value = true
            parkings.value = getAllParkingsUseCase()!!
            isLoading.value = false
        }

    }

    fun parkingCercanos(direccion: Direccion) {
        viewModelScope.launch {
            isLoading.postValue(true)
            var cacheParkingsCercanos: List<Parking>? = null
            parkings.value?.forEach { p -> establecerDistancia(direccion, p) }
            parkings.value = parkings.value?.sortedBy { it.distancia }
            if (parkings.value?.size!! >= 10) {
                cacheParkingsCercanos = parkings.value?.take(10)
            }
            cacheParkingsCercanos?.forEach { p -> establecerDireccion(p) }
            closestParkings.value = cacheParkingsCercanos!!
            isLoading.postValue(false)
        }
    }

//    fun buscarParkings(direccion: Direccion) {
//        viewModelScope.launch {
//            isLoading.postValue(true)
//            closestParkings.value = getClosestParkingsUseCase(direccion.lat, direccion.lon)
//            isLoading.postValue(false)
//            closestParkings.value?.forEach { p -> establecerDistancia(direccion, p) }
//            closestParkings.postValue(closestParkings.value?.sortedBy { it.distancia })
//        }
//    }

    fun establecerDistancia(direccion: Direccion, parking: Parking) {
        val distancia: FloatArray = floatArrayOf(0f)
        Location.distanceBetween(direccion.lat, direccion.lon, parking.lat, parking.lon, distancia)
        parking.distancia = distancia[0]
    }

    fun establecerDireccion(parking: Parking) {
        parkingCargados.postValue(false)
        viewModelScope.launch {
            val reverseDireccion = getReverseDireccionUseCase(parking.lon, parking.lat)
            parking.direccion = Direccion(
                reverseDireccion.id,
                parking.lat,
                parking.lon,
                reverseDireccion.numero,
                reverseDireccion.calle,
                reverseDireccion.codigoPostal
            )
            if(parking.direccion!!.calle.isNullOrEmpty() || parking.direccion!!.numero.isNullOrEmpty()) {
                parking.direccion!!.calle = reverseDireccion.name
            }
            parkingCargados.postValue(true)
        }
    }

    fun getReverseDireccion(parking: Parking) {
        viewModelScope.launch {
           reverseDireccion.value  = getReverseDireccionUseCase(parking.lon, parking.lat)!!
        }
    }
}