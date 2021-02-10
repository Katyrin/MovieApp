package com.katyrin.movieapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.katyrin.movieapp.R
import com.katyrin.movieapp.model.Movie

class MainRVAdapter(
    private val moviesList: List<Movie>, private val onClickListener: FilmOnClickListener
): RecyclerView.Adapter<MainRVAdapter.MainViewHolder>() {

    inner class MainViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val movieName: TextView = view.findViewById(R.id.movieName)
        val rating: TextView = view.findViewById(R.id.ratingTextView)
        val year: TextView = view.findViewById(R.id.yearTextView)
        val cardView: CardView = view.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item, parent, false)
        return MainViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.movieName.text = moviesList[position].movieName
        holder.rating.text = moviesList[position].rating.toString()
        holder.year.text = moviesList[position].year.toString()
        holder.cardView.setOnClickListener {
            onClickListener.onFilmClicked(moviesList[position])
        }
    }

    override fun getItemCount(): Int = moviesList.size
}