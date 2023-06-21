package ar.edu.uade.da2023.starwarsapp.data

import ar.edu.uade.da2023.starwarsapp.model.Character
import retrofit2.Call
import retrofit2.http.GET

interface StarWarsAPI {
    @GET("people")
    fun getCharacters(): Call<ArrayList<Character>>
}