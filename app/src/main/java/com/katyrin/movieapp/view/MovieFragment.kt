package com.katyrin.movieapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.katyrin.movieapp.databinding.MovieFragmentBinding
import com.katyrin.movieapp.model.BUNDLE_EXTRA
import com.katyrin.movieapp.model.IMAGE_BASE_URL
import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.viewmodel.MovieViewModel
import com.squareup.picasso.Picasso

class MovieFragment : Fragment() {

    private lateinit var binding: MovieFragmentBinding

    companion object {
        fun newInstance(bundle: Bundle): MovieFragment {
            val movieFragment = MovieFragment()
            movieFragment.arguments = bundle
            return movieFragment
        }
    }

    private val viewModel: MovieViewModel by lazy {
        ViewModelProvider(this).get(MovieViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = MovieFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.getParcelable<Movie>(BUNDLE_EXTRA)?.let { viewModel.setData(it) }
        val observer = Observer<Movie> {renderData(it)}
        viewModel.getData().observe(viewLifecycleOwner, observer)
    }

    private fun renderData(movie: Movie) {
        binding.movieName.text = movie.title
        binding.description.text = movie.overview
        binding.ratingTextView.text = movie.voteAverage
        binding.yearTextView.text = movie.releaseDate
        Picasso.get()
            .load(IMAGE_BASE_URL + movie.posterPath)
            .into(binding.itemImageView)
    }

}