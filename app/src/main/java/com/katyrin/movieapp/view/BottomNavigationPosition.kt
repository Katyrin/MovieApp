package com.katyrin.movieapp.view

import com.katyrin.movieapp.R

enum class BottomNavigationPosition(val id: Int) {
    MOVIES(R.id.movies),
    FAVORITES(R.id.favorites),
    HISTORY(R.id.history),
    MAP(R.id.map)
}

fun findNavigationPositionById(id: Int) = when (id) {
    BottomNavigationPosition.MOVIES.id -> BottomNavigationPosition.MOVIES
    BottomNavigationPosition.FAVORITES.id -> BottomNavigationPosition.FAVORITES
    BottomNavigationPosition.HISTORY.id -> BottomNavigationPosition.HISTORY
    BottomNavigationPosition.MAP.id -> BottomNavigationPosition.MAP
    else -> BottomNavigationPosition.MOVIES
}

fun BottomNavigationPosition.createFragment() = when (this) {
    BottomNavigationPosition.MOVIES -> MainFragment.newInstance()
    BottomNavigationPosition.FAVORITES -> FavoritesFragment.newInstance()
    BottomNavigationPosition.HISTORY -> HistoryFragment.newInstance()
    BottomNavigationPosition.MAP -> MapsFragment.newInstance()
}

fun BottomNavigationPosition.getTag() = when (this) {
    BottomNavigationPosition.MOVIES -> MainFragment.TAG
    BottomNavigationPosition.FAVORITES -> FavoritesFragment.TAG
    BottomNavigationPosition.HISTORY -> HistoryFragment.TAG
    BottomNavigationPosition.MAP -> MapsFragment.TAG
}


