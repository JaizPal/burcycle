package com.example.burparking.ui.viewModel

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burparking.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuscarDireccionViewModel @Inject constructor(
    private val getDireccionesUserCase: GetAllDireccionesUseCase,
    private val getClosestParkingsUseCase: GetClosestParkingsUseCase,
    private val getAllParkingsUseCase: GetAllParkingsUseCase
) :
    ViewModel() {

    val direcciones = MutableLiveData<List<Direccion>>()
    val closestParkings = MutableLiveData<List<Parking>>()
    val parkings = MutableLiveData<List<Parking>>()
    val isLoading = MutableLiveData<Boolean>()

    fun onCreate() {
        viewModelScope.launch {
            isLoading.postValue(true)
            direcciones.value = getDireccionesUserCase()!!
            isLoading.postValue(false)
        }
        viewModelScope.launch {
            isLoading.postValue(true)
            parkings.value = getAllParkingsUseCase()!!
            isLoading.postValue(false)
        }

    }

    fun parkingCercanos(direccion: Direccion) {
        viewModelScope.launch {
            isLoading.postValue(true)
            parkings.value?.forEach { p -> establecerDistancia(direccion, p) }
            parkings.value = parkings.value?.sortedBy { it.distancia }
            if(parkings.value?.size!! >= 10) {
                parkings.value = parkings.value?.take(10)
            }
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
}