package ar.edu.uade.da2023.starwarsapp.data

import ar.edu.uade.da2023.starwarsapp.model.Character

class CharactersRepository {
    private val charactersDataSource = CharactersDataSource()

    suspend fun getCharacters(): ArrayList<Character>? {
        return charactersDataSource.getCharacters()
    }
}