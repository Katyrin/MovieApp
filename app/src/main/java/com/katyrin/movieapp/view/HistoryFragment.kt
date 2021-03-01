package com.katyrin.movieapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.katyrin.movieapp.databinding.HistoryFragmentBinding
import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.viewmodel.AppState
import com.katyrin.movieapp.viewmodel.HistoryViewModel

class HistoryFragment : Fragment() {

    private val viewModel: HistoryViewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }
    private val adapter: HistoryRVAdapter by lazy { HistoryRVAdapter() }
    private lateinit var binding: HistoryFragmentBinding
    private lateinit var listHistoryMovie: List<Movie>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = HistoryFragmentBinding.inflate(inflater)
        if (savedInstanceState != null) {
            listHistoryMovie = savedInstanceState.getParcelableArrayList("HISTORY_MOVIES")!!
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.historyRV.adapter = adapter
        viewModel.getHistoryData().observe(viewLifecycleOwner, { renderHistoryData(it) })
        viewModel.getNoteData().observe(viewLifecycleOwner, { renderNoteData(it) })
        viewModel.getAllHistory()
    }

    private fun renderHistoryData(appState: AppState) {
        when (appState) {
            is AppState.SuccessSearch -> {
                binding.historyRV.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE

                listHistoryMovie = appState.movies
                viewModel.getAllNotes()
            }
            is AppState.Loading -> {
                binding.historyRV.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.historyRV.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.historyRV.createAndShow(
                    "Error", "Reload",
                    {
                        viewModel.getAllHistory()
                    })
            }
        }
    }

    private fun renderNoteData(appState: AppState) {
        when (appState) {
            is AppState.SuccessSearch -> {
                binding.historyRV.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE

                startRVAdapter(appState.movies)
            }
            is AppState.Loading -> {
                binding.historyRV.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.historyRV.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.historyRV.createAndShow(
                    "Error", "Reload",
                    {
                        viewModel.getAllHistory()
                    })
            }
        }
    }

    private fun startRVAdapter(moviesNote: List<Movie>) {
        moviesNote.map { note ->
            listHistoryMovie.map {
                if (it.idMovie == note.idMovie) {
                    it.filmNote = note.filmNote
                }
            }
        }
        adapter.setData(listHistoryMovie)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val arrayListMovies: ArrayList<Movie> = arrayListOf()
        arrayListMovies.addAll(listHistoryMovie)
        outState.putParcelableArrayList("HISTORY_MOVIES", arrayListMovies)
    }

    companion object {
        val TAG: String = HistoryFragment::class.java.simpleName
        fun newInstance() = HistoryFragment()
    }
}