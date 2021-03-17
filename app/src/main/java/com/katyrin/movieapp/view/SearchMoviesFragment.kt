package com.katyrin.movieapp.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.katyrin.movieapp.R
import com.katyrin.movieapp.databinding.SearchMoviesFragmentBinding
import com.katyrin.movieapp.model.*
import com.katyrin.movieapp.viewmodel.AppState
import com.katyrin.movieapp.viewmodel.SearchMoviesViewModel

class SearchMoviesFragment : Fragment() {

    companion object {
        private var searchText: String = ""
        fun newInstance(searchText: String): SearchMoviesFragment {
            this.searchText = searchText
            return SearchMoviesFragment()
        }
    }

    private var listFavoritesMovie: List<Movie> = listOf()
    private lateinit var binding: SearchMoviesFragmentBinding
    private val viewModel: SearchMoviesViewModel by lazy {
        ViewModelProvider(this).get(SearchMoviesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = SearchMoviesFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val newText = "${getString(R.string.found_on_request)} $searchText"
        binding.searchTextView.text = newText

        viewModel.liveDataToObserve.observe(viewLifecycleOwner, { renderData(it) })
        viewModel.getFavoritesData().observe(viewLifecycleOwner, { renderData(it) })
        viewModel.getAllFavorites()
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.SuccessFavorites -> {
                listFavoritesMovie = appState.movies
                getMoviesWithSettings()
            }
            is AppState.SuccessSearch -> {
                binding.loadingLayout.visibility = View.GONE
                binding.searchMoviesRV.visibility = View.VISIBLE
                setData(appState.movies)
            }
            is AppState.Loading -> {
                binding.searchMoviesRV.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.searchMoviesRV.createAndShow(
                    getString(R.string.error), getString(R.string.reload),
                    {
                        viewModel.getAllFavorites()
                    })
            }
            else -> {
                binding.searchMoviesRV.createAndShow(getString(R.string.unknown_app_state))
            }
        }
    }

    private fun getMoviesWithSettings() {
        activity?.let {
            viewModel.getSearchMoviesRemoteSource(
                getString(R.string.language),
                it.getSharedPreferences(SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                    .getBoolean(IS_SHOW_ADULT_CONTENT, false),
                searchText,
                it.getSharedPreferences(SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                    .getInt(MIN_YEAR_RESULT, 1895).toString() + "-01-01")
        }
    }

    private fun setData(movies: List<Movie>) {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.searchMoviesRV.layoutManager = layoutManager
        binding.searchMoviesRV.adapter =
            SearchMoviesRVAdapter(movies, listFavoritesMovie, onLikeListener, onClickListener)
        binding.searchMoviesRV.createAndShow(getString(R.string.success), length = Snackbar.LENGTH_LONG)
    }

    private val onClickListener = object : FilmOnClickListener {
        override fun onFilmClicked(movie: Movie) {
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_EXTRA, movie)

            activity?.supportFragmentManager?.apply {
                beginTransaction()
                    .add(R.id.container, MovieFragment.newInstance(bundle))
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }
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