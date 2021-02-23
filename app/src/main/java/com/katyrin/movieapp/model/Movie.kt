package com.katyrin.movieapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val movieName: String = "",
    val rating: String = "",
    val description: String = "",
    val year: String = "",
    val genre: String = ""
): Parcelable
