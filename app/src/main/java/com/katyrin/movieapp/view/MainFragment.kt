package com.katyrin.movieapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.katyrin.movieapp.R
import com.katyrin.movieapp.model.MoviesData
import com.katyrin.movieapp.viewmodel.AppState
import com.katyrin.movieapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val observer = Observer<AppState> {renderData(it)}
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getMoviesFromLocalSource()
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                val moviesData = appState.movies
                loadingLayout.visibility = View.GONE
                setData(moviesData)
            }
            is AppState.Loading -> {
                loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                loadingLayout.visibility = View.GONE
                requireView().createAndShow(
                        "Error", "Reload",
                        { viewModel.getMoviesFromLocalSource() },
                        Snackbar.LENGTH_INDEFINITE
                )
            }
        }
    }

    private fun setData(moviesData: MoviesData) {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mainRecyclerView.layoutManager = layoutManager
        mainRecyclerView.adapter = VerticalRVAdapter(moviesData.genres)

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