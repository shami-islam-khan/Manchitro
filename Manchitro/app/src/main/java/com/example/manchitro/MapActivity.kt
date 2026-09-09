package com.example.manchitro

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.manchitro.json.PlaceDataItem
import com.example.manchitro.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapActivity : AppCompatActivity(), MapListener {

    lateinit var mMap: MapView
    lateinit var controller: IMapController
    lateinit var mMyLocationOverlay: MyLocationNewOverlay
    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Status_bar_color
        window.statusBarColor = resources.getColor(R.color.bd_green, theme)
        // Navigation_bar_color
        window.navigationBarColor = resources.getColor(R.color.white, theme)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )

        mMap = findViewById(R.id.osmmap)
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.setMultiTouchControls(true)

        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mMap)
        controller = mMap.controller

        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.enableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true
        mMyLocationOverlay.runOnFirstFix {
            runOnUiThread {
                controller.setCenter(mMyLocationOverlay.myLocation)
                controller.animateTo(mMyLocationOverlay.myLocation)
            }
        }

        // Set initial map center and zoom level
        val startPoint = GeoPoint(23.6850, 90.3563) // Default location (Dhaka, Bangladesh)
        controller.setCenter(startPoint)
        controller.setZoom(18.0)

        mMap.overlays.add(mMyLocationOverlay)
        mMap.addMapListener(this)

        sharedPrefsHelper = SharedPrefsHelper(this)

        // Load entities
        loadEntities()
    }

    private fun loadEntities() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entities = RetrofitInstance.api.getEntities()
                withContext(Dispatchers.Main) {
                    displayEntities(entities)
                }
                sharedPrefsHelper.savePlaces(entities)
            } catch (e: Exception) {
                Log.e("MapActivity", "Error loading entities: ${e.message}")
                val cachedEntities = sharedPrefsHelper.loadPlaces()
                if (cachedEntities != null) {
                    withContext(Dispatchers.Main) {
                        displayEntities(cachedEntities)
                    }
                }
            }
        }
    }

    private fun displayEntities(entities: List<PlaceDataItem>) {
        mMap.overlays.clear() // Clear existing overlays
        entities.forEach { entity ->
            val marker = Marker(mMap)
            marker.position = GeoPoint(entity.lat, entity.lon)
            marker.title = entity.title
            marker.icon = resizeMarkerIcon(R.drawable.bd_marker_icon, 40, 50)
            marker.setOnMarkerClickListener { _, _ ->
                showEntityImage(entity)
                true
            }
            mMap.overlays.add(marker)
        }
        mMap.invalidate() // Refresh map
    }

    private fun showEntityImage(entity: PlaceDataItem) {
        if (entity.image.isNotEmpty()) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_image, null)
            val imageView = dialogView.findViewById<ImageView>(R.id.entity_image)

            Glide.with(this)
                .load("https://labs.anontech.info/cse489/t3/" + entity.image) // Concatenate image path
                .into(imageView)

            AlertDialog.Builder(this)
                .setTitle(entity.title)
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .show()
        } else {
            Toast.makeText(this, "No image available for ${entity.title}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addEntity() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_entity, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.et_title)
        val etLatitude = dialogView.findViewById<EditText>(R.id.et_latitude)
        val etLongitude = dialogView.findViewById<EditText>(R.id.et_longitude)

        AlertDialog.Builder(this)
            .setTitle("Add New Entity")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = etTitle.text.toString()
                val latitude = etLatitude.text.toString().toDoubleOrNull() ?: mMap.mapCenter.latitude
                val longitude = etLongitude.text.toString().toDoubleOrNull() ?: mMap.mapCenter.longitude

                val titleRequestBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val latRequestBody = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val lonRequestBody = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitInstance.api.createEntity(
                            titleRequestBody,
                            latRequestBody,
                            lonRequestBody,
                            null
                        )
                        if (response.isSuccessful) {
                            val createdEntity = response.body()!!
                            withContext(Dispatchers.Main) {
                                val marker = Marker(mMap)
                                marker.position = GeoPoint(createdEntity.lat, createdEntity.lon)
                                marker.title = createdEntity.title
                                marker.icon = resizeMarkerIcon(R.drawable.bd_marker_icon, 40, 50)
                                marker.setOnMarkerClickListener { _, _ ->
                                    showEntityImage(createdEntity)
                                    true
                                }
                                mMap.overlays.add(marker)
                                mMap.invalidate() // Refresh map
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@MapActivity, "Error creating entity", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MapActivity, "Error creating entity: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun resizeMarkerIcon(drawableRes: Int, width: Int, height: Int): Drawable {
        val bitmap = (resources.getDrawable(drawableRes, theme) as BitmapDrawable).bitmap
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
        return BitmapDrawable(resources, resizedBitmap)
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        Log.e("TAG", "onScroll latitude: ${event?.source?.mapCenter?.latitude}")
        Log.e("TAG", "onScroll longitude: ${event?.source?.mapCenter?.longitude}")
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        Log.e("TAG", "onZoom zoom level: ${event?.zoomLevel}")
        return false
    }
}
