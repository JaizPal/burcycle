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

    fun onCreate() {
        db.collection("frases").document("O1BAXWjN6Lsg90PNA6hi").get().addOnSuccessListener {
//            frases.add(Frase(it.getString("parte1"), it.getString("parte2"), it.getString("parte3"), it.getString("autor")))
            frasesRaw.value = it.get("frases") as List<HashMap<String, String>>
            Log.i("FraseA", frasesRaw.value?.size.toString())
            frasesRaw.value?.forEach{
                Log.i("Frase", it.toString())
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

    private fun setFrase() {

            val fraseAleatoria = frases.random()
            parte1.value = fraseAleatoria.parte1
            parte2.value = fraseAleatoria.parte2
            autor.value = fraseAleatoria.autor



    }

}