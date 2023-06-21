package ar.edu.uade.da2023.starwarsapp.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.edu.uade.da2023.starwarsapp.data.CharactersRepository
import ar.edu.uade.da2023.starwarsapp.model.Character
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlin.coroutines.CoroutineContext

class MainViewModel: ViewModel() {
    private val charactersRepository = CharactersRepository()
    private val coroutineContext: CoroutineContext = newSingleThreadContext("starwars")
    private val scope = CoroutineScope(coroutineContext)
    private val _TAG = "MainViewModel - StarWarsApp"

    var characters = MutableLiveData<ArrayList<Character>>()

    fun onStart() {
        scope.launch {
            kotlin.runCatching {
                charactersRepository.getCharacters()
            }.onSuccess {
                Log.d(_TAG, "Characters onSuccess")
                characters.postValue(it)
            }.onFailure {
                Log.e(_TAG, "Charactes onFailure")
            }

        }
    }
}