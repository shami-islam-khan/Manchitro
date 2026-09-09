package com.example.manchitro

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manchitro.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class FormActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText
    private lateinit var imageView: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnSubmit: Button

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        // Status_bar_color
        window.statusBarColor = resources.getColor(R.color.bd_green, theme)
        // Navigation_bar_color
        window.navigationBarColor = resources.getColor(R.color.white, theme)

        etTitle = findViewById(R.id.et_title)
        etLatitude = findViewById(R.id.et_latitude)
        etLongitude = findViewById(R.id.et_longitude)
        imageView = findViewById(R.id.imageView)
        btnSelectImage = findViewById(R.id.btn_select_image)
        btnSubmit = findViewById(R.id.btn_submit)

        btnSelectImage.setOnClickListener {
            openImagePicker()
        }

        btnSubmit.setOnClickListener {
            submitForm()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            imageView.setImageURI(imageUri)

            // Convert URI to file path
            selectedImageFile = uriToFile(imageUri)
        }
    }

    private fun uriToFile(uri: Uri): File {
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        val file = File(cacheDir, "image.jpg")
        val outputStream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }

    private fun submitForm() {
        val title = etTitle.text.toString()
        val latitude = etLatitude.text.toString().toDouble()
        val longitude = etLongitude.text.toString().toDouble()

        val titleRequestBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val latRequestBody = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val lonRequestBody = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        var imageRequestBody: MultipartBody.Part? = null
        if (selectedImageFile != null) {
            val requestFile = selectedImageFile!!.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            imageRequestBody = MultipartBody.Part.createFormData("image", selectedImageFile!!.name, requestFile)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.createEntity(
                    titleRequestBody,
                    latRequestBody,
                    lonRequestBody,
                    imageRequestBody
                )
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@FormActivity, "Entity created", Toast.LENGTH_SHORT).show()
                        // Clear the form and reset the image file
                        etTitle.text.clear()
                        etLatitude.text.clear()
                        etLongitude.text.clear()
                        imageView.setImageResource(0) // clear image
                        selectedImageFile = null
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@FormActivity, "Error creating entity", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@FormActivity, "Error creating entity: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
