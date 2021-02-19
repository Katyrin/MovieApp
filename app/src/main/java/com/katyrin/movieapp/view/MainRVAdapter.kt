package com.katyrin.movieapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.katyrin.movieapp.R
import com.katyrin.movieapp.model.ResultsDTO

class MainRVAdapter(
    private val moviesList: Array<ResultsDTO?>, private val onClickListener: FilmOnClickListener
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
        holder.movieName.text = moviesList[position]?.title
        holder.rating.text = moviesList[position]?.vote_average
        holder.year.text = moviesList[position]?.release_date
        holder.cardView.setOnClickListener {
            moviesList[position]?.let { it1 -> onClickListener.onFilmClicked(it1) }
        }
    }

    override fun getItemCount(): Int = moviesList.size
}