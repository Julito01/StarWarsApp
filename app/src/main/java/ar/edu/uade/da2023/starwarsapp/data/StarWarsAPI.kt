package ar.edu.uade.da2023.starwarsapp.data

import ar.edu.uade.da2023.starwarsapp.model.JsonResponse
import ar.edu.uade.da2023.starwarsapp.model.Planet
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface StarWarsAPI {
    @GET("people")
    fun getCharacters(): Call<JsonResponse>

    @GET
    fun getHomeworld(
        @Url url: String
    ): Call<Planet>
}