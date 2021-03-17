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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.katyrin.movieapp.R
import com.katyrin.movieapp.databinding.MainFragmentBinding
import com.katyrin.movieapp.model.*
import com.katyrin.movieapp.viewmodel.AppState
import com.katyrin.movieapp.viewmodel.MainViewModel
import java.util.*

class MainFragment : Fragment() {

    companion object {
        val TAG: String = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }

    private var listFavoritesMovie: List<Movie> = listOf()
    private lateinit var binding: MainFragmentBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.liveDataToObserve.observe(viewLifecycleOwner, { renderData(it) })
        viewModel.favoritesLiveData.observe(viewLifecycleOwner, { renderData(it) })
        viewModel.getAllFavorites()
    }

    private fun getMoviesWithSettings() {
        activity?.let {
            viewModel.getGenresFromRemoteSource(getString(R.string.language),
                it.getSharedPreferences(SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                    .getBoolean(IS_SHOW_ADULT_CONTENT, false),
                it.getSharedPreferences(SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                    .getInt(VOTE_AVERAGE, 0),
                it.getSharedPreferences(SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                    .getInt(MIN_YEAR_RESULT, 1895).toString() + "-01-01")
        }
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.SuccessFavorites -> {
                listFavoritesMovie = appState.movies
                getMoviesWithSettings()
            }
            is AppState.SuccessMainQuery -> {
                binding.loadingLayout.visibility = View.GONE
                binding.mainRecyclerView.visibility = View.VISIBLE
                setData(appState.movies)
                requireView().createAndShow(getString(R.string.success), length = Snackbar.LENGTH_LONG)
            }
            is AppState.Loading -> {
                binding.mainRecyclerView.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.mainRecyclerView.createAndShow(
                    getString(R.string.error), getString(R.string.reload),
                    {
                        getMoviesWithSettings()
                    })
            }
            else -> {
                binding.mainRecyclerView.createAndShow(getString(R.string.unknown_app_state))
            }
        }
    }

    private fun setData(genres: SortedMap<Genre, List<Movie>>) {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.mainRecyclerView.layoutManager = layoutManager
        binding.mainRecyclerView.adapter =
            VerticalRVAdapter(genres, listFavoritesMovie, onClickListener, onLikeListener)
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