package com.example.burparking.ui.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.burparking.domain.model.Frase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PrincipalViewModel @Inject constructor(): ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private var frasesRaw = MutableLiveData<List<HashMap<String, String>>>()
    private val frases: ArrayList<Frase> = arrayListOf()
    var parte1 = MutableLiveData<String?>()
    var parte2 = MutableLiveData<String?>()
    var autor = MutableLiveData<String?>()

    /*
     * Descarga las frases de la BBDD y las guarda en this.frases
     */
    fun onCreate() {
        db.collection("frases").document("O1BAXWjN6Lsg90PNA6hi").get().addOnSuccessListener {
            frasesRaw.value = it.get("frases") as List<HashMap<String, String>>
            frasesRaw.value?.forEach{
                frases.add(
                    Frase(
                        it.getValue("parte1"),
                        it.getValue("parte2"),
                        it.getValue("autor")
                    )
                )
            }
            setFrase()
        }
    }

    /*
     * Recupera una frase aleatoria de this.frases
     * y establece sus diferentes partes y autor.
     */
    private fun setFrase() {
        val fraseAleatoria = frases.random()
        parte1.value = fraseAleatoria.parte1
        parte2.value = fraseAleatoria.parte2
        autor.value = fraseAleatoria.autor
    }

}