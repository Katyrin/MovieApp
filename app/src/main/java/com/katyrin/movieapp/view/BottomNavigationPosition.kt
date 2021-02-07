package com.katyrin.movieapp.view

import androidx.fragment.app.Fragment
import com.katyrin.movieapp.R

enum class BottomNavigationPosition(val id: Int) {
    MOVIES(R.id.movies),
    FAVORITES(R.id.favorites),
    RATING(R.id.rating)
}

fun findNavigationPositionById(id: Int): BottomNavigationPosition = when (id) {
    BottomNavigationPosition.MOVIES.id -> BottomNavigationPosition.MOVIES
    BottomNavigationPosition.FAVORITES.id -> BottomNavigationPosition.FAVORITES
    BottomNavigationPosition.RATING.id -> BottomNavigationPosition.RATING
    else -> BottomNavigationPosition.MOVIES
}

fun BottomNavigationPosition.createFragment(): Fragment = when (this) {
    BottomNavigationPosition.MOVIES -> MainFragment.newInstance()
    BottomNavigationPosition.FAVORITES -> FavoritesFragment.newInstance()
    BottomNavigationPosition.RATING -> RatingFragment.newInstance()
}

fun BottomNavigationPosition.getTag(): String = when (this) {
    BottomNavigationPosition.MOVIES -> MainFragment.TAG
    BottomNavigationPosition.FAVORITES -> FavoritesFragment.TAG
    BottomNavigationPosition.RATING -> RatingFragment.TAG
}


