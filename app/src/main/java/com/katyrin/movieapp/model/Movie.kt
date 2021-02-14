package com.katyrin.movieapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val movieName: String,
    val rating: Int,
    val description: String,
    val year: Int,
    val genre: String
): Parcelable
