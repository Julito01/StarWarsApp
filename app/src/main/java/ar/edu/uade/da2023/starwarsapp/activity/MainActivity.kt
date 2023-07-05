package ar.edu.uade.da2023.starwarsapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.uade.da2023.starwarsapp.R
import com.google.firebase.FirebaseApp
import ar.edu.uade.da2023.starwarsapp.model.Character
import ar.edu.uade.da2023.starwarsapp.model.Resource
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var rvCharacters: RecyclerView
    private lateinit var adapter: CharactersAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var characters: ArrayList<Character> = ArrayList()
    private var favoriteCharacters: ArrayList<Character> = ArrayList()
    private lateinit var firebaseAuth: FirebaseAuth
    var firestore = FirebaseFirestore.getInstance()
    private val _TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        bindView()
        bindViewModel()
        bindButtons()
    }

    override fun onStart() {
        super.onStart()
    }

    private fun bindButtons() {
        val exitButton = findViewById<Button>(R.id.exitButton)
        exitButton.setOnClickListener {
            exitApplication()
        }

        val favoritesButton = findViewById<Button>(R.id.favoritesButton)
        val favoriteLabel: String = getString(R.string.favorites_button);
        val homeLabel: String = getString(R.string.home_button)
        favoritesButton.text = favoriteLabel;
        favoritesButton.setOnClickListener {
            if (favoritesButton.text == favoriteLabel) {
                favoritesButton.text = homeLabel
                val stringCharacters = sharedPreferences.getString("favorites_characters", null)
                val characters =
                    Gson().fromJson(stringCharacters, Array<Character>::class.java).toMutableList()
                adapter.update(characters, true)
            } else {
                val stringCharacters = sharedPreferences.getString("character_list", null)
                val characters =
                    Gson().fromJson(stringCharacters, Array<Character>::class.java).toMutableList()
                favoritesButton.text = favoriteLabel
                adapter.update(characters, false)
            }
        }
    }

    private fun bindViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        sharedPreferences = getSharedPreferences("characters_cache", Context.MODE_PRIVATE)

        val characterList = sharedPreferences.getString("character_list", null)
        if (characterList.isNullOrEmpty()) {
            val image: ImageView = findViewById(R.id.loadingImage)
            image.visibility = View.VISIBLE
            Glide.with(this).load(R.drawable.splash).into(image)
            viewModel.onStart()
            viewModel.characters.observe(this) { it ->
                image.visibility = View.GONE
                val userDocument = adapter.getUserDocument()
                val resourcesDocument = firestore.collection("resources").document("images")

                Log.d(_TAG, "Calling firebase to create lists")
                resourcesDocument
                    .get()
                    .addOnSuccessListener { document ->
                        val resourcesData = mutableListOf<Resource>()
                        val data = document.data?.get("data") as? List<Map<String, Any>>

                        if (data != null) {
                            for (item in data) {
                                val resource = Resource(
                                    item["quote"] as String,
                                    item["id"] as Long,
                                    item["url"] as String
                                )
                                resourcesData.add(resource)
                            }
                        }

                        Log.d("MainActivity", "$resourcesData")
                        var counter = 0
                        for (char in it) {
                            char.quote = resourcesData[counter].quote
                            char.imageUrl = resourcesData[counter].imageUrl
                            counter += 1
                        }

                        userDocument
                            ?.get()
                            ?.addOnSuccessListener { document ->
                                Log.d("MainActivity", "Document: $document")
                                val favorites =
                                    document.data?.get("favorites") as? List<Map<String, Any>>

                                var id = 1
                                for (char in it) {
                                    char.id = id
                                    val isFavorite = favorites?.any { it["name"] == char.name }
                                    if (isFavorite == true) {
                                        favoriteCharacters.add(char)
                                        char.isFavorite = true
                                    }
                                    id += 1
                                }

                                characters = it
                                adapter.update(it, false)
                                val jsonCharacters = Gson().toJson(it)
                                val jsonFavoriteCharacters = Gson().toJson(favoriteCharacters)
                                sharedPreferences.edit().putString("character_list", jsonCharacters)
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("favorites_characters", jsonFavoriteCharacters).apply()
                                Log.d(_TAG, "$it")
                            }
                    }
            }
        } else {
            val cachedCharacters = Gson().fromJson(characterList, Array<Character>::class.java).toList()
            characters = cachedCharacters as ArrayList<Character>
            adapter.update(cachedCharacters, false)
        }
    }

    private fun bindView() {
        rvCharacters = findViewById(R.id.rvCharacters)
        rvCharacters.layoutManager = LinearLayoutManager(this)
        adapter = CharactersAdapter(this, characters) { itemDto: Character, position: Int ->
            Log.d("MyActivity", "Clicked on item ${itemDto.name} at position $position")
            val intent: Intent = Intent(this@MainActivity, CharacterActivity::class.java)
            intent.putExtra("characterId", itemDto.id)
            startActivity(intent)
        }
        rvCharacters.adapter = adapter
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            val db = FirebaseFirestore.getInstance()
            val userCollection = db.collection("users")
            val userDocument = userCollection.document(firebaseUser.uid)

            userDocument.get().addOnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.exists()) {
                    val userData = hashMapOf(
                        "email" to firebaseUser.email,
                        "favorites" to emptyList<Map<String, Any>>()
                    )

                    userDocument.set(userData)
                        .addOnSuccessListener {
                            Log.d("MainActivity", "User collection created and user stored")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("MainActivity", "User collection couldn't be created")
                        }
                }
            }
        }
    }

    private fun exitApplication() {
        // Cerrar sesión en Firebase Authentication
        FirebaseAuth.getInstance().signOut()


        // Cerrar sesión en Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            // Cerrar completamente la aplicación
            finishAffinity()
        }
    }
}