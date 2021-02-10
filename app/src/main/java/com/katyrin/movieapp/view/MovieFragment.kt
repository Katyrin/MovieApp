package com.katyrin.movieapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.katyrin.movieapp.R
import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.viewmodel.MovieViewModel
import kotlinx.android.synthetic.main.movie_fragment.*

class MovieFragment : Fragment() {

    companion object {
        fun newInstance() = MovieFragment()
    }

    private lateinit var viewModel: MovieViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.movie_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        arguments?.getParcelable<Movie>("movie")?.let { viewModel.setData(it) }
        val observer = Observer<Movie> {renderData(it)}
        viewModel.getData().observe(viewLifecycleOwner, observer)
    }

    private fun renderData(movie: Movie) {
        movieName.text = movie.movieName
        description.text = movie.description
        ratingTextView.text = movie.rating.toString()
        yearTextView.text = movie.year.toString()
    }

}