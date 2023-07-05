package ar.edu.uade.da2023.starwarsapp.activity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import ar.edu.uade.da2023.starwarsapp.R
import ar.edu.uade.da2023.starwarsapp.data.CharactersRepository
import ar.edu.uade.da2023.starwarsapp.model.Character
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlin.coroutines.CoroutineContext

class CharacterActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var _TAG: String = "CharacterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character)

        sharedPreferences = getSharedPreferences("characters_cache", Context.MODE_PRIVATE)
        val characterId = intent.getIntExtra("characterId", 1)
        Log.d(_TAG, "Id: $characterId")
        val stringCharacters = sharedPreferences.getString("character_list", null)
        Log.d(_TAG, "$stringCharacters")
        val characters = Gson().fromJson(stringCharacters, Array<Character>::class.java).asList()
        val character = characters[characterId - 1]
        Log.d(_TAG, "$character")
        setText(character)
        loadImage(character)
    }

    private fun loadImage(character: Character) {
        val image: ImageView = findViewById(R.id.characterImg)
        Glide.with(this)
            .load(character.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(image)
    }

    private fun setText(character: Character) {
        findViewById<TextView>(R.id.characterNameText).text = character.name
        findViewById<TextView>(R.id.quoteText).text = character.quote
        findViewById<TextView>(R.id.worldText).text = getString(R.string.world_label_text).format(character.homeWorld)
        findViewById<TextView>(R.id.yearText).text = getString(R.string.year_label_text).format(character.birthYear)
        findViewById<TextView>(R.id.hairColorText).text = getString(R.string.hair_label_text).format(character.hairColor)
        findViewById<TextView>(R.id.genderText).text = getString(R.string.gender_label_text).format(character.gender)
    }
}