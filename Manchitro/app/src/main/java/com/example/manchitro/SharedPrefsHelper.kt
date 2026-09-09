package com.example.manchitro

import android.content.Context
import com.example.manchitro.json.PlaceDataItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefsHelper(context: Context) {

    private val prefs = context.getSharedPreferences("places_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun savePlaces(places: List<PlaceDataItem>) {
        val json = gson.toJson(places)
        prefs.edit().putString("places_key", json).apply()
    }

    fun loadPlaces(): List<PlaceDataItem>? {
        val json = prefs.getString("places_key", null)
        return if (json != null) {
            val type = object : TypeToken<List<PlaceDataItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }
}
