package ar.edu.uade.da2023.starwarsapp.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import ar.edu.uade.da2023.starwarsapp.R
import com.bumptech.glide.Glide

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        showGif()

        val userName = "jdechert"

        val preferences = getSharedPreferences(
            "ar.edu.uade.da2023.starwarsapp.sharedpreferences",
            Context.MODE_PRIVATE
        )
        preferences.edit().putString("user", userName)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 4000)
    }

    fun showGif() {
        val image: ImageView = findViewById(R.id.splashGif)
        Glide.with(this).load(R.drawable.splash).into(image)
    }
}