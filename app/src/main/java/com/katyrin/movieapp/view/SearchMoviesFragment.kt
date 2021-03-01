package com.katyrin.movieapp.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.katyrin.movieapp.R
import com.katyrin.movieapp.databinding.SearchMoviesFragmentBinding
import com.katyrin.movieapp.model.BUNDLE_EXTRA
import com.katyrin.movieapp.model.IS_SHOW_ADULT_CONTENT
import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.model.SETTINGS_SHARED_PREFERENCE
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
        getMoviesWithSettings()
    }

    private fun getMoviesWithSettings() {
        activity?.let {
            viewModel.getSearchMoviesRemoteSource(
                    getString(R.string.language),
                    it.getSharedPreferences(SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                            .getBoolean(IS_SHOW_ADULT_CONTENT, false),
                    searchText)
        }
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.SuccessSearch -> {
                binding.loadingLayout.visibility = View.GONE
                setData(appState.movies)
            }
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                binding.loadingLayout.createAndShow(
                    "Error", "Reload",
                    { getMoviesWithSettings() },
                    Snackbar.LENGTH_INDEFINITE
                )
            }
        }
    }

    private fun setData(movies: List<Movie>) {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.searchMoviesRV.layoutManager = layoutManager
        binding.searchMoviesRV.adapter = SearchMoviesRVAdapter(movies, object : FilmOnClickListener {
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
        binding.searchMoviesRV.createAndShow("Success", length = Snackbar.LENGTH_LONG)
    }
}