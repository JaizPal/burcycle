package com.example.burparking.data.model.direccion

import javax.inject.Inject
import javax.inject.Singleton

/*
 * Proveedor de las direcciones
 */
@Singleton
class DireccionProvider @Inject constructor(){
    var direcciones: List<DireccionElementModel> = emptyList()
}