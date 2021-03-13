package com.katyrin.movieapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Cinema(
    val placeName: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
): Parcelable