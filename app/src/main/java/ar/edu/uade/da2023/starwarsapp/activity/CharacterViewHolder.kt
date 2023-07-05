package ar.edu.uade.da2023.starwarsapp.activity

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ar.edu.uade.da2023.starwarsapp.R

class CharacterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var image: ImageView = itemView.findViewById(R.id.charImage)
    var favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteButton)
    var name: TextView = itemView.findViewById(R.id.charName)
    var height: TextView = itemView.findViewById(R.id.charHeight)
    var birthYear: TextView = itemView.findViewById(R.id.charYear)
}