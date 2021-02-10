package com.katyrin.movieapp.model

import android.os.Parcel
import android.os.Parcelable

data class Movie(
    val movieName: String?,
    val rating: Int,
    val description: String?,
    val year: Int,
    val genre: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(movieName)
        parcel.writeInt(rating)
        parcel.writeString(description)
        parcel.writeInt(year)
        parcel.writeString(genre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}
