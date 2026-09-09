package com.example.manchitro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Status_bar_color
        window.statusBarColor = resources.getColor(R.color.bd_green, theme)
        // Navigation_bar_color
        window.navigationBarColor = resources.getColor(R.color.bd_green, theme)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000) // Display splash screen for 3 seconds
    }
}
