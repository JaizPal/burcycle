package com.example.burparking.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.burparking.domain.model.Parking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class IncidenciaViewModel @Inject constructor(): ViewModel() {

    val tiposIncidencias = MutableLiveData<List<String>>()
    private val db = FirebaseFirestore.getInstance()
    val succes = MutableLiveData<Int>()

    fun onCreate() {
        succes.value = 2
        db.collection("tipoIncidencias").document("tipoIncidencias").get().addOnSuccessListener {
            tiposIncidencias.value = it.get("tipo") as List<String>?
        }
    }

    fun addIncidencia(descripcion: String, tipo: String, parking: Parking?) {
        db.collection("incidencias").document(SimpleDateFormat(
            "dd-MM-yyyy HH:mm:ss",
            Locale.getDefault()
        ).format(Date())
        ).set(hashMapOf(
            "descripcion" to descripcion,
            "tipo" to tipo,
            "usuario" to FirebaseAuth.getInstance().currentUser?.email,
            "idParking" to parking?.id
        )).addOnSuccessListener {
            succes.value = 0
        }.addOnCanceledListener {
            succes.value = -1
            succes.value = 2
        }

    }

}