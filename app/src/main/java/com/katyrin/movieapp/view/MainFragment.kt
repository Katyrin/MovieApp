package com.katyrin.movieapp.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var binding: MainFragmentBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = MainFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.liveDataToObserve.observe(viewLifecycleOwner, { renderData(it) })
        getMoviesWithSettings()
    }

    private fun getMoviesWithSettings() {
        activity?.let {
            viewModel.getGenresFromRemoteSource(getString(R.string.language),
                    it.getSharedPreferences(SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                            .getBoolean(IS_SHOW_ADULT_CONTENT, false),
                    it.getSharedPreferences(SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                            .getInt(VOTE_AVERAGE, 0)
            )
        }
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.loadingLayout.visibility = View.GONE
                setData(appState.movies)
            }
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                requireView().createAndShow(
                    "Error", "Reload", { getMoviesWithSettings() },
                    Snackbar.LENGTH_INDEFINITE)
            }
        }
    }

    private fun setData(genres: SortedMap<Genre, List<Movie>>) {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.mainRecyclerView.layoutManager = layoutManager
        binding.mainRecyclerView.adapter = VerticalRVAdapter(genres)

        requireView().createAndShow("Success", length = Snackbar.LENGTH_LONG)
    }
}