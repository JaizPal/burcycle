package com.example.burparking.data.model.reversedireccion

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReverseDireccionProvider @Inject constructor() {
    var features: List<ReverseDireccionFeaturesModel> = emptyList()
}