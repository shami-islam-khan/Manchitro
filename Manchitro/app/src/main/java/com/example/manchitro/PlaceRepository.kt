package com.example.manchitro.repository

import android.content.Context
import com.example.manchitro.database.PlaceDatabase
import com.example.manchitro.entities.PlaceEntity
import com.example.manchitro.json.PlaceDataItem
import com.example.manchitro.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaceRepository(private val context: Context) {

    private val placeDao = PlaceDatabase.getDatabase(context).placeDao()

    suspend fun fetchPlaces(): List<PlaceDataItem> {
        return try {
            val places = fetchPlacesFromNetwork()
            savePlacesToLocalDatabase(places)
            places
        } catch (e: Exception) {
            loadPlacesFromLocalDatabase()
        }
    }

    private suspend fun fetchPlacesFromNetwork(): List<PlaceDataItem> {
        return withContext(Dispatchers.IO) {
            RetrofitInstance.api.getEntities()
        }
    }

    private suspend fun savePlacesToLocalDatabase(places: List<PlaceDataItem>) {
        val placeEntities = places.map {
            PlaceEntity(it.id, it.title, it.lat, it.lon, it.image)
        }
        placeDao.insertPlaces(placeEntities)
    }

    private suspend fun loadPlacesFromLocalDatabase(): List<PlaceDataItem> {
        return withContext(Dispatchers.IO) {
            val placeEntities = placeDao.getAllPlaces()
            placeEntities.map {
                PlaceDataItem(it.id, it.title, it.lat, it.lon, it.image)
            }
        }
    }
}
