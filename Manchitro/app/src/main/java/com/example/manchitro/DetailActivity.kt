package com.example.manchitro

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.manchitro.json.PlaceDataItem
import com.example.manchitro.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {

    private lateinit var placeDataItem: PlaceDataItem
    private lateinit var etTitle: EditText
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText
    private lateinit var imageView: ImageView
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Status_bar_color
        window.statusBarColor = resources.getColor(R.color.bd_green, theme)
        // Navigation_bar_color
        window.navigationBarColor = resources.getColor(R.color.white, theme)

        placeDataItem = intent.getSerializableExtra("placeDataItem") as PlaceDataItem

        etTitle = findViewById(R.id.et_title)
        etLatitude = findViewById(R.id.et_latitude)
        etLongitude = findViewById(R.id.et_longitude)
        imageView = findViewById(R.id.imageView)
        btnSave = findViewById(R.id.btn_save)

        etTitle.setText(placeDataItem.title)
        etLatitude.setText(placeDataItem.lat.toString())
        etLongitude.setText(placeDataItem.lon.toString())
        Glide.with(this).load("https://labs.anontech.info/cse489/t3/" + placeDataItem.image).into(imageView)

        btnSave.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val title = etTitle.text.toString()
        val latitude = etLatitude.text.toString().toDouble()
        val longitude = etLongitude.text.toString().toDouble()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.updateEntity(
                    placeDataItem.id,
                    title,
                    latitude,
                    longitude
                )
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DetailActivity, "Changes saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DetailActivity, "Error saving changes", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailActivity, "Error saving changes: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
