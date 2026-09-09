package com.example.manchitro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manchitro.json.PlaceDataItem
import com.example.manchitro.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlaceDataAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Status_bar_color
        window.statusBarColor = resources.getColor(R.color.bd_green, theme)
        // Navigation_bar_color
        window.navigationBarColor = resources.getColor(R.color.white, theme)

        databaseHelper = DatabaseHelper(this)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = PlaceDataAdapter { placeDataItem ->
            Log.d("ListActivity", "Details button clicked for item: ${placeDataItem.id}")
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("placeDataItem", placeDataItem)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        loadEntitiesFromServer()
    }

    private fun loadEntitiesFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entities = RetrofitInstance.api.getEntities()
                databaseHelper.clearPlaces()
                for (entity in entities) {
                    databaseHelper.addPlace(entity)
                }
                runOnUiThread {
                    loadEntitiesFromLocal()
                }
            } catch (e: Exception) {
                Log.e("ListActivity", "Error loading entities: ${e.message}")
                runOnUiThread {
                    loadEntitiesFromLocal()
                }
            }
        }
    }

    private fun loadEntitiesFromLocal() {
        val entities = databaseHelper.getAllPlaces()
        adapter.setData(entities)
    }
}
