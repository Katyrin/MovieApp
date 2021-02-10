package com.katyrin.movieapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.katyrin.movieapp.R

class FavoritesFragment: Fragment() {

    companion object {
        val TAG: String = FavoritesFragment::class.java.simpleName
        fun newInstance() = FavoritesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.favorites_fragment, container, false)
    }
}