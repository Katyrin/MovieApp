package com.katyrin.movieapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.katyrin.movieapp.R
import com.katyrin.movieapp.databinding.RvVerticalBinding
import com.katyrin.movieapp.model.BUNDLE_EXTRA
import com.katyrin.movieapp.model.Genre
import com.katyrin.movieapp.model.Movie
import java.util.*

class VerticalRVAdapter(private val genres: SortedMap<Genre, List<Movie>>
): RecyclerView.Adapter<VerticalRVAdapter.VerticalViewHolder>() {

    inner class VerticalViewHolder(private val itemBinding: RvVerticalBinding):
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(genre: Genre) {
            itemBinding.genre.text = genre.name.capitalize(Locale.ROOT)
        }
        fun bind(moviesList: List<Movie>) {
            val context = itemBinding.horizontalRV.context
            val layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            itemBinding.horizontalRV.layoutManager = layoutManager
            itemBinding.horizontalRV.adapter = MainRVAdapter(moviesList, object : FilmOnClickListener {
                override fun onFilmClicked(movie: Movie) {
                    val bundle = Bundle()
                    bundle.putParcelable(BUNDLE_EXTRA, movie)

                    val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    val movieFragment = MovieFragment.newInstance(bundle)
                    transaction.replace(R.id.container, movieFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            })
        }
    }

    override fun getItemCount(): Int = genres.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = RvVerticalBinding.inflate(layoutInflater, parent, false)
        return VerticalViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: VerticalViewHolder, position: Int) {
        val genre: Genre = genres.keys.elementAt(position)
        holder.bind(genre)
        val moviesList: List<Movie> = genres[genre]!!
        holder.bind(moviesList)
    }
}