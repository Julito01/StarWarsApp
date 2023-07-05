package ar.edu.uade.da2023.starwarsapp.data

import android.util.Log
import ar.edu.uade.da2023.starwarsapp.model.Character
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

class CharactersDataSource {

    private val _BASE_URL = "https://swapi.dev/api/";
    private val _TAG = "API-STARWARS"
    private val starWarsAPI: StarWarsAPI

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        starWarsAPI = retrofit.create(StarWarsAPI::class.java)
    }

    suspend fun getCharacters(): ArrayList<Character>? {
        Log.d(_TAG, "Characters DataSource GET")

        val response = starWarsAPI.getCharacters().execute()

        if (!response.isSuccessful) {
            Log.e(_TAG, response.message())
            return ArrayList<Character>()
        }

        Log.d(_TAG, "Characters DataSource Success")
        val characters = response.body()?.results

        if (characters != null) {
            for (character in characters) {
                character.homeWorld = getHomeworld(character.homeWorld)
            }
        }

        return characters
    }

    suspend fun getHomeworld(url: String): String {
        Log.d(_TAG, "Planets DataSource GET")

        val response = starWarsAPI.getHomeworld(url).execute()

        if (!response.isSuccessful) {
            Log.e(_TAG, response.message())
            return "n/a"
        }

        Log.d(_TAG, "Planet DataSource Success")
        return response.body()?.name ?: "n/a"
    }
}