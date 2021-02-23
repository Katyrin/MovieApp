package com.katyrin.movieapp.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.katyrin.movieapp.R
import com.katyrin.movieapp.model.*
import com.katyrin.movieapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    companion object {
        val TAG: String = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }

    private lateinit var genreBundle: Genre
    private val loadResultsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.getStringExtra(DETAILS_LOAD_RESULT_EXTRA)) {
                DETAILS_INTENT_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_DATA_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_RESPONSE_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_REQUEST_ERROR_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_REQUEST_ERROR_MESSAGE_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_URL_MALFORMED_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_RESPONSE_SUCCESS_EXTRA -> renderData(
                    GenresDTO(
                        intent.getParcelableArrayExtra(DETAILS_GENRES_LIST_EXTRA) as Array<GenreDTO?>
                    )
                )
                else -> TODO(PROCESS_ERROR)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .registerReceiver(loadResultsReceiver, IntentFilter(DETAILS_INTENT_FILTER))
        }
    }

    override fun onDestroy() {
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(loadResultsReceiver)
        }
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        genreBundle = arguments?.getParcelable(BUNDLE_EXTRA) ?: Genre()
        getGenres()
    }

    private fun getGenres() {
        loadingLayout?.apply { visibility = View.VISIBLE }
        context?.let {
            it.startService(Intent(it, GenresService::class.java))
        }
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

//        val observer = Observer<AppState> { renderData(it)}
//        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getMoviesFromLocalSource()
    }

    private fun renderData(genresDTO: GenresDTO) {
        loadingLayout?.apply { visibility = View.GONE }
        val genres = genresDTO.genres

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mainRecyclerView?.apply {
            this.layoutManager = layoutManager
            adapter = VerticalRVAdapter(genres)
        }

        view?.createAndShow("Success", length = Snackbar.LENGTH_LONG)
    }

//    private fun renderData(appState: AppState) {
//        when (appState) {
//            is AppState.Success -> {
//                val moviesData = appState.movies
//                loadingLayout.visibility = View.GONE
//                setData(moviesData)
//            }
//            is AppState.Loading -> {
//                loadingLayout.visibility = View.VISIBLE
//            }
//            is AppState.Error -> {
//                loadingLayout.visibility = View.GONE
//                requireView().createAndShow(
//                        "Error", "Reload",
//                        { viewModel.getMoviesFromLocalSource() },
//                        Snackbar.LENGTH_INDEFINITE
//                )
//            }
//        }
//    }

//    private fun setData(moviesData: MoviesData) {
//        val layoutManager = LinearLayoutManager(requireContext())
//        layoutManager.orientation = LinearLayoutManager.VERTICAL
//        mainRecyclerView.layoutManager = layoutManager
//        mainRecyclerView.adapter = VerticalRVAdapter(moviesData.genres)
//
//        requireView().createAndShow("Success", length = Snackbar.LENGTH_LONG)
//    }

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