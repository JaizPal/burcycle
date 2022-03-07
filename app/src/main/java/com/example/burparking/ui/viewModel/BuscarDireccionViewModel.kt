package com.example.burparking.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burparking.domain.model.Direccion
import com.example.burparking.domain.model.GetAllDireccionesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuscarDireccionViewModel @Inject constructor(private val getDireccionesUserCase: GetAllDireccionesUseCase) :
    ViewModel() {

    val direcciones = MutableLiveData<List<Direccion>>()
    val isLoading = MutableLiveData<Boolean>()

    fun onCreate() {
        viewModelScope.launch {
            isLoading.postValue(true)
            direcciones.value = getDireccionesUserCase()!!
            isLoading.postValue(false)
        }
    }
}