package com.example.burparking.data.model.direccion

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DireccionProvider @Inject constructor(){
    var direcciones: List<DireccionElementModel> = emptyList()
}