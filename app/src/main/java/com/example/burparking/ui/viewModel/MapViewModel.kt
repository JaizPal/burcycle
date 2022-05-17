package com.example.burparking.ui.viewModel

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burparking.domain.model.Direccion
import com.example.burparking.domain.model.GetAllParkingsUseCase
import com.example.burparking.domain.model.GetReverseDireccionUseCase
import com.example.burparking.domain.model.Parking
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAllParkingsUseCase: GetAllParkingsUseCase,
    private val getReverseDireccionUseCase: GetReverseDireccionUseCase
) : ViewModel() {
    val parkings = MutableLiveData<List<Parking>>()
    var parkingCargados = MutableLiveData<Boolean>()
    var parkingPulsado = MutableLiveData<Parking>()

    /*
     * Recupera todos los aparcamientos
     */
    fun onCreate() {
        viewModelScope.launch {
            parkingCargados.value = false
            parkings.value = getAllParkingsUseCase()!!
            parkingCargados.value = true
        }
    }

    /*
     * Si el aparcamiento mandado por parámetros no tiene establecida
     * una dirección o un número se hace uso de la dirección inversa
     */
    fun establecerDireccion(parking: Parking) {
        parkingCargados.postValue(false)
        viewModelScope.launch {
            val reverseDireccion = getReverseDireccionUseCase(parking.lon, parking.lat)
            parking.direccion = Direccion(
                parking.id,
                parking.lat,
                parking.lon,
                reverseDireccion.numero,
                reverseDireccion.calle,
                reverseDireccion.codigoPostal
            )
            if (parking.direccion!!.calle.isNullOrEmpty() || parking.direccion!!.numero.isNullOrEmpty()) {
                parking.direccion!!.calle = reverseDireccion.name
            }
            parkingPulsado.value = parking
            parkingCargados.postValue(true)
        }
    }

    fun setParking(id: Long) {
        establecerDireccion(parkings.value?.find { it.id == id }!!)
    }

    /*
     * Dibuja la ruta desde la dirección hasta el aparcamiento.
     */
    fun setRoad(geoPoints: ArrayList<GeoPoint>, map: MapView, context: Context) {
        viewModelScope.launch {
            val roadManager: RoadManager =
                OSRMRoadManager(context, Configuration.getInstance().userAgentValue)
            (roadManager as OSRMRoadManager).setMean(OSRMRoadManager.MEAN_BY_BIKE)
            val road = roadManager.getRoad(geoPoints)
            val roadOverlay = RoadManager.buildRoadOverlay(road)
            roadOverlay.width = 12.0f
            roadOverlay.color = Color.parseColor("#03adfc")
            map.overlays.add(roadOverlay)
            map.invalidate()
        }
    }
}