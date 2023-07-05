package ar.edu.uade.da2023.starwarsapp.activity

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import ar.edu.uade.da2023.starwarsapp.R
import ar.edu.uade.da2023.starwarsapp.model.Character
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.lang.reflect.Field

class CharactersAdapter(
    val context: Context,
    objects: ArrayList<Character>,
    val clickListener: (Character, Int) -> Unit
) : RecyclerView.Adapter<CharacterViewHolder>() {
    var items: MutableList<Character> = ArrayList<Character>()
    var hasFilter: Boolean = false
    var firestore = FirebaseFirestore.getInstance();
    var usersCollection = firestore.collection("users");
    private lateinit var firebaseAuth: FirebaseAuth
    var onItemClick: ((Character) -> Unit)? = null
    var characters: List<Character> = emptyList()
    lateinit var sharedPreferences: SharedPreferences
    var _TAG = "CharactersAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.character_item, parent, false)
        return CharacterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun displayData(holder: CharacterViewHolder, position: Int) {
        holder.name.text = items[position].name
        holder.height.text = items[position].height.toString()
        holder.birthYear.text = items[position].birthYear
        Glide.with(holder.itemView.context).load(items[position].imageUrl).into(holder.image)

        if (!items[position].isFavorite) {
            Glide.with(holder.itemView.context).load(R.drawable.favorite_outline)
                .into(holder.favoriteIcon)
        } else {
            Glide.with(holder.itemView.context).load(R.drawable.favorite_fill)
                .into(holder.favoriteIcon)
        }
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        sharedPreferences = context.getSharedPreferences("characters_cache", Context.MODE_PRIVATE)
        displayData(holder, position)
        holder.itemView.setOnClickListener { clickListener(items[position], position) }

        holder.favoriteIcon.setOnClickListener {
            val userDocument = getUserDocument()
            if (userDocument != null) {
                if (!items[position].isFavorite) {
                    addCharacter(userDocument, items[position], holder)

                } else {
                    removeCharacter(userDocument, items[position], holder)
                }
            }
        }
    }

    private fun addCharacter(
        document: DocumentReference,
        item: Character,
        holder: CharacterViewHolder
    ) {
        item.isFavorite = true
        document
            .update("favorites", FieldValue.arrayUnion(item))
            .addOnSuccessListener {
                Log.d(_TAG, "Character stored!")
                val favoriteCharactersString =
                    sharedPreferences.getString("favorites_characters", null)

                val favoriteCharacters =
                    Gson().fromJson(favoriteCharactersString, Array<Character>::class.java)
                        .toMutableList()

                favoriteCharacters.add(item)

                sharedPreferences.edit()
                    .putString("favorites_characters", Gson().toJson(favoriteCharacters)).apply()

                val charactersString =
                    sharedPreferences.getString("character_list", null)

                val characters =
                    Gson().fromJson(charactersString, Array<Character>::class.java)
                        .toMutableList()

                characters[item.id - 1].isFavorite = true

                sharedPreferences.edit()
                    .putString("character_list", Gson().toJson(characters))
                    .apply()

                Glide.with(holder.itemView.context).load(R.drawable.favorite_fill)
                    .into(holder.favoriteIcon)
            }
    }

    private fun removeCharacter(
        document: DocumentReference,
        item: Character,
        holder: CharacterViewHolder
    ) {
        document
            .update("favorites", FieldValue.arrayRemove(item))
            .addOnSuccessListener {
                Log.d(_TAG, "Character removed!")
                val favoriteCharactersString =
                    sharedPreferences.getString("favorites_characters", null)

                val favoriteCharacters =
                    Gson().fromJson(favoriteCharactersString, Array<Character>::class.java)
                        .toMutableList()

                favoriteCharacters.remove(item)

                sharedPreferences.edit()
                    .putString("favorites_characters", Gson().toJson(favoriteCharacters))
                    .apply()

                val charactersString =
                    sharedPreferences.getString("character_list", null)

                val characters =
                    Gson().fromJson(charactersString, Array<Character>::class.java)
                        .toMutableList()

                characters[item.id - 1].isFavorite = false

                sharedPreferences.edit()
                    .putString("character_list", Gson().toJson(characters))
                    .apply()

                Log.d(_TAG, "Filter activated: $hasFilter")

                if (!hasFilter) {
                    update(characters, false)
                } else {
                    update(favoriteCharacters, true)
                }

                Glide.with(holder.itemView.context).load(R.drawable.favorite_outline)
                    .into(holder.favoriteIcon)
            }
    }

    fun getUserDocument(): DocumentReference? {
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser ?: return null
        return usersCollection.document(user.uid)
    }

    fun update(items: MutableList<Character>, hasFilter: Boolean) {
        this.items = items
        this.hasFilter = hasFilter
        this.notifyDataSetChanged()
    }
}