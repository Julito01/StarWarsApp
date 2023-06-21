package ar.edu.uade.da2023.starwarsapp.model

import com.google.gson.annotations.SerializedName

data class Character(
    val name: String,
    val height: Int,
    val mass: Int,

    @SerializedName("hair_color")
    val hairColor: String,

    @SerializedName("eye_color")
    val eyeColor: String,

    @SerializedName("birth_year")
    val birthYear: String,

    val gender: String,

    @SerializedName("homeworld")
    val homeWorld: String
)