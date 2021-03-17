package com.katyrin.movieapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.katyrin.movieapp.R
import com.katyrin.movieapp.databinding.FavoritesFragmentBinding
import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.viewmodel.AppState
import com.katyrin.movieapp.viewmodel.FavoritesViewModel

class FavoritesFragment: Fragment() {

    companion object {
        val TAG: String = FavoritesFragment::class.java.simpleName
        fun newInstance() = FavoritesFragment()
    }

    private val viewModel: FavoritesViewModel by lazy {
        ViewModelProvider(this).get(FavoritesViewModel::class.java)
    }
    private val adapter: MoviesListRVAdapter by lazy { MoviesListRVAdapter(onLikeListener) }
    private lateinit var binding: FavoritesFragmentBinding
    private var listFavoritesMovie: List<Movie> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FavoritesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.favoriteRV.adapter = adapter
        viewModel.getFavoritesData().observe(viewLifecycleOwner, { renderData(it) })
        viewModel.getNoteData().observe(viewLifecycleOwner, { renderData(it) })
        viewModel.getAllFavorites()
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.SuccessFavorites -> {
                listFavoritesMovie = appState.movies
                viewModel.getAllNotes()
            }
            is AppState.SuccessNote -> {
                binding.favoriteRV.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE

                startRVAdapter(appState.movies)
            }
            is AppState.Loading -> {
                binding.favoriteRV.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.favoriteRV.createAndShow(
                    getString(R.string.error), getString(R.string.reload),
                    {
                        viewModel.getAllFavorites()
                    })
            }
            else -> {
                binding.favoriteRV.createAndShow(getString(R.string.unknown_app_state))
            }
        }
    }

    private fun startRVAdapter(moviesNote: List<Movie>) {
        moviesNote.map { note ->
            listFavoritesMovie.map {
                if (it.idMovie == note.idMovie) {
                    it.filmNote = note.filmNote
                }
            }
        }
        adapter.setData(listFavoritesMovie, listFavoritesMovie)
    }

    private val onLikeListener = object : FavoriteFilmOnClickListener {
        override fun onFilmLiked(movie: Movie, favoriteImageView: ImageView) {

            favoriteImageView.setImageDrawable(ResourcesCompat.getDrawable(
                favoriteImageView.resources, R.drawable.ic_baseline_favorite_24,
                null))
            viewModel.saveFavoriteMovieToDB(movie)

            listFavoritesMovie.map {
                if (it.idMovie == movie.idMovie) {
                    favoriteImageView.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            favoriteImageView.resources, R.drawable.ic_baseline_favorite_border_24,
                            null
                        )
                    )
                    viewModel.deleteFavoriteMovieToDB(movie.idMovie)
                }
            }
        }
    }
}