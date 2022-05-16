package com.example.burparking.data.model.reversedireccion

import javax.inject.Inject
import javax.inject.Singleton

/*
 * Proveedor de las reverseDirecciones
 */
@Singleton
class ReverseDireccionProvider @Inject constructor() {
    var features: List<ReverseDireccionFeaturesModel> = emptyList()
}