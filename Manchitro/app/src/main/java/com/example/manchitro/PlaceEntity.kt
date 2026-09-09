package com.example.manchitro.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val lat: Double,
    val lon: Double,
    val image: String
)
