package com.example.burparking.data.model.parking

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParkingProvider @Inject constructor() {
    var parkings: List<ParkingElementModel> = emptyList()
}