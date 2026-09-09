package com.example.manchitro

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.manchitro.network.ApiService
import com.google.android.material.navigation.NavigationView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var buttonDrawerToggle: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Status_bar_color
        window.statusBarColor = resources.getColor(R.color.bd_green, theme)
        // Navigation_bar_color
        window.navigationBarColor = resources.getColor(R.color.white, theme)

        // Run gif function
        showGIF()

        drawerLayout = findViewById(R.id.drawerLayout)
        buttonDrawerToggle = findViewById(R.id.menu_button)
        navigationView = findViewById(R.id.nav_view)

        // Set the navigation view listener
        navigationView.setNavigationItemSelectedListener(this)

        // Initialize ActionBarDrawerToggle
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Enable the menu button as the home button and show it
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Click for open navigation drawer
        buttonDrawerToggle.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://labs.anontech.info/cse489/t3/") // Replace with your actual base URL.
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }

    // Function to animate gif image
    private fun showGIF() {
        val imageView: ImageView = findViewById(R.id.imageView2)
        Glide.with(this).load(R.drawable.manchitro_home).into(imageView)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_map -> {
                // Navigate to MapActivity
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_form -> {
                // Navigate to FormActivity
                val intent = Intent(this, FormActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_list -> {
                // Navigate to ListActivity
                val intent = Intent(this, ListActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
