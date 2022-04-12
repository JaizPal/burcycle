package com.example.burparking.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.burparking.domain.model.Incidencia
import com.example.burparking.domain.model.Parking
import com.example.burparking.domain.model.Reporte
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class InformacionViewModel @Inject constructor() : ViewModel() {

    var parking: Parking? = null
    val reportesRaw = MutableLiveData<List<HashMap<String, Any>>>()
    val reportes: ArrayList<Reporte> = arrayListOf()
    var reporte: Reporte? = null
    var capacidadMedia = MutableLiveData<Double>()
    var capacidadUltimoReporte = MutableLiveData<Int>()
    var fechaUltimoReporte = MutableLiveData<Date>()
    private val db = FirebaseFirestore.getInstance()
    var incidencias = MutableLiveData<ArrayList<Incidencia>>()
    var incidenciasCargadas = MutableLiveData<Boolean>()

    fun onCreate() {
        db.collection("reportes").document("reportes").get().addOnSuccessListener {
            reportesRaw.value = it.get("reportes") as List<HashMap<String, Any>>?
            reportesRaw.value?.forEach {
                reportes.add(
                    Reporte(
                        it["idParking"].toString().toLong(),
                        it["capacidad"].toString().toInt(),
                        (it["fechaReporte"] as Timestamp).toDate()
                    )
                )
            }
            setInformacion()
            setCapacidadMedia()
        }

        db.collection("incidencias").get().addOnSuccessListener {
            val incidenciasRaw= it.documents
            incidenciasCargadas.postValue(false)
            incidencias.value = arrayListOf()
            incidenciasRaw.forEach{
                val incidencia = Incidencia(it.getString("idParking")?.toLong(),it.id, it.getString("tipo")!!, it.getString("descripcion")!!)
                if (incidencia.idParking == parking?.id) {
                    incidencias.value?.add(incidencia)
                }
                incidenciasCargadas.postValue(true)
            }

        }

    }

    private fun setInformacion() {
        reporte = reportes.find { it.idParking == parking?.id }
        capacidadUltimoReporte.postValue(reporte?.capacidad)
        fechaUltimoReporte.postValue(reporte?.fechaReporte)
    }

    private fun setCapacidadMedia() {
        val reportesFiltrados: ArrayList<Reporte> = arrayListOf()
        var capacidadMediaReporte = 0.0
        reportes.forEach {
            if (it.idParking == parking?.id) {
                reportesFiltrados.add(it)
                capacidadMediaReporte = capacidadMediaReporte.plus(it.capacidad.toDouble())

            }
        }
        capacidadMediaReporte /= reportesFiltrados.size
        capacidadMedia.postValue(capacidadMediaReporte)


    }
}