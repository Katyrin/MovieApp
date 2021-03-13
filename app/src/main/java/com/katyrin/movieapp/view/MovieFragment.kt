package com.katyrin.movieapp.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.katyrin.movieapp.databinding.MovieFragmentBinding
import com.katyrin.movieapp.model.BUNDLE_EXTRA
import com.katyrin.movieapp.model.IMAGE_BASE_URL
import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.viewmodel.AppState
import com.katyrin.movieapp.viewmodel.MovieViewModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class MovieFragment : Fragment() {

    companion object {
        fun newInstance(bundle: Bundle): MovieFragment {
            val movieFragment = MovieFragment()
            movieFragment.arguments = bundle
            return movieFragment
        }
    }

    private lateinit var movie: Movie
    private lateinit var binding: MovieFragmentBinding
    private val viewModel: MovieViewModel by lazy {
        ViewModelProvider(this).get(MovieViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MovieFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.getParcelable<Movie>(BUNDLE_EXTRA)?.let { movie = it }
        initViews()

        val observer = Observer<AppState> {renderData(it)}
        viewModel.getData().observe(viewLifecycleOwner, observer)
        viewModel.getAllNotes()
    }

    private fun initViews() {
        saveMovie(movie)

        binding.movieName.text = movie.title
        binding.description.text = movie.overview
        binding.ratingTextView.text = movie.voteAverage
        binding.yearTextView.text = movie.releaseDate
        Picasso.get()
            .load(IMAGE_BASE_URL + movie.posterPath)
            .into(binding.itemImageView)

        hideNote()

        binding.enterNoteEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                movie.filmNote = binding.enterNoteEditText.text.toString()
                saveNote(movie)
                showNote()

                binding.myNote.text = movie.filmNote

                hideKeypad()
            }
            return@setOnEditorActionListener true
        }

        binding.editNoteButton.setOnClickListener {
            binding.enterNoteEditText.setText(binding.myNote.text)
            hideNote()
            showKeypad()
        }
    }

    private fun showNote() {
        binding.myNote.visibility = View.VISIBLE
        binding.editNoteButton.visibility = View.VISIBLE
        binding.enterNoteEditText.visibility = View.GONE
        binding.enterNoteInputLayout.visibility = View.GONE
    }

    private fun hideNote() {
        binding.myNote.visibility = View.GONE
        binding.editNoteButton.visibility = View.GONE
        binding.enterNoteEditText.visibility = View.VISIBLE
        binding.enterNoteInputLayout.visibility = View.VISIBLE
    }

    private fun hideKeypad() {
        val inputManager = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val binder = requireView().windowToken
        inputManager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun showKeypad() {
        binding.enterNoteEditText.requestFocus()
        val mgr = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.showSoftInput(binding.enterNoteEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.SuccessNote -> {
                checkNote(appState.movies)
            }
            is AppState.SuccessMainQuery -> { }
            is AppState.SuccessFavorites -> { }
            is AppState.SuccessSearch -> { }
            is AppState.SuccessHistory -> { }
            is AppState.Loading -> { }
            is AppState.Error -> {
                requireView().createAndShow(
                    "Error", "Reload", { viewModel.getData() },
                    Snackbar.LENGTH_INDEFINITE
                )
            }
        }
    }

    private fun checkNote(movies: List<Movie>) {

        movies.map {
            if (it.idMovie == movie.idMovie){
                showNote()
                binding.myNote.text = it.filmNote
            }
        }
    }

    private fun saveMovie(movie: Movie) {
        movie.dateSearching =
            SimpleDateFormat("dd/M/yyyy hh:mm:ss a", Locale.getDefault()).format(Date())
        viewModel.saveMovieToDB(movie)
    }

    private fun saveNote(movie: Movie) {
        viewModel.saveNoteToDB(Movie(idMovie = movie.idMovie, filmNote = movie.filmNote))
    }
}