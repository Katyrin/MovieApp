package com.katyrin.movieapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.katyrin.movieapp.R
import com.katyrin.movieapp.model.Genre
import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.viewmodel.AppState
import com.katyrin.movieapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.*

class MainFragment : Fragment() {

    companion object {
        val TAG: String = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.liveDataToObserve.observe(viewLifecycleOwner, { renderData(it) })
        viewModel.getGenresFromRemoteSource(getString(R.string.language))
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                loadingLayout.visibility = View.GONE
                setData(appState.movies)
            }
            is AppState.Loading -> {
                loadingLayout.visibility = View.VISIBLE
            }
            is AppState.LoadingSecondQuery -> {

            }
            is AppState.Error -> {
                loadingLayout.visibility = View.GONE
                requireView().createAndShow(
                        "Error", "Reload",
                        { viewModel.getGenresFromRemoteSource(getString(R.string.language)) },
                        Snackbar.LENGTH_INDEFINITE
                )
            }
        }
    }

    private fun setData(genres: SortedMap<Genre, List<Movie>>) {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mainRecyclerView.layoutManager = layoutManager
        mainRecyclerView.adapter = VerticalRVAdapter(genres)

        requireView().createAndShow("Success", length = Snackbar.LENGTH_LONG)
    }

    private fun View.createAndShow(text: String, actionText: String = "",
                                   action: ((View) -> Unit)? = null,
                                   length: Int = Snackbar.LENGTH_INDEFINITE) {
        Snackbar.make(this, text, length).also {
            if (action != null) it.setAction(actionText, action)
        }.apply {
            anchorView = requireActivity().findViewById(R.id.bottomNavigation)
        }.show()
    }
}