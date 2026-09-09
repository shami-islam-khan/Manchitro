package com.example.manchitro.json

import java.io.Serializable

data class PlaceDataItem(
    val id: Int,
    var title: String,
    var lat: Double,
    var lon: Double,
    var image: String
) : Serializable
