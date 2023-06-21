package ar.edu.uade.da2023.starwarsapp.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import ar.edu.uade.da2023.starwarsapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

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
}