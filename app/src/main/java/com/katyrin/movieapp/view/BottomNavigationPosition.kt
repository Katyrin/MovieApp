package com.katyrin.movieapp.view

import com.katyrin.movieapp.R

enum class BottomNavigationPosition(val id: Int) {
    MOVIES(R.id.movies),
    FAVORITES(R.id.favorites),
    RATING(R.id.rating)
}

fun findNavigationPositionById(id: Int) = when (id) {
    BottomNavigationPosition.MOVIES.id -> BottomNavigationPosition.MOVIES
    BottomNavigationPosition.FAVORITES.id -> BottomNavigationPosition.FAVORITES
    BottomNavigationPosition.RATING.id -> BottomNavigationPosition.RATING
    else -> BottomNavigationPosition.MOVIES
}

fun BottomNavigationPosition.createFragment() = when (this) {
    BottomNavigationPosition.MOVIES -> MainFragment.newInstance()
    BottomNavigationPosition.FAVORITES -> FavoritesFragment.newInstance()
    BottomNavigationPosition.RATING -> RatingFragment.newInstance()
}

fun BottomNavigationPosition.getTag() = when (this) {
    BottomNavigationPosition.MOVIES -> MainFragment.TAG
    BottomNavigationPosition.FAVORITES -> FavoritesFragment.TAG
    BottomNavigationPosition.RATING -> RatingFragment.TAG
}


