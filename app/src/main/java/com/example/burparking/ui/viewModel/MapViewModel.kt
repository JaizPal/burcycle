package com.example.burparking.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burparking.domain.model.GetAllParkingsUseCase
import com.example.burparking.domain.model.Parking
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAllParkingsUseCase: GetAllParkingsUseCase
): ViewModel() {
    val parkings = MutableLiveData<List<Parking>>()
    var parkingCargados = MutableLiveData<Boolean>()

    fun onCreate() {
        viewModelScope.launch {
            parkingCargados.value = false
            parkings.value = getAllParkingsUseCase()!!
            parkingCargados.value = true
        }
    }
}