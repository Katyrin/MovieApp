package com.katyrin.movieapp.view

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.katyrin.movieapp.databinding.SetMarkerDialogBinding

interface OnMarkerDialogListener {
    fun setPositiveButton(dialogBinding: SetMarkerDialogBinding, latLng: LatLng,
                          marker: Marker?) : Boolean
}