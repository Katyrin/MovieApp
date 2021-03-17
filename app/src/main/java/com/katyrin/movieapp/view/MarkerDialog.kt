package com.katyrin.movieapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.katyrin.movieapp.databinding.SetMarkerDialogBinding

class MarkerDialog(
    private val title: String,
    private val message: String,
    private val editTextVisibility: Int,
    private val latLng: LatLng = LatLng(0.0, 0.0),
    private val  marker: Marker? = null
): BottomSheetDialogFragment() {

    private lateinit var dialogListener: OnMarkerDialogListener
    private lateinit var binding: SetMarkerDialogBinding

    fun setOnDialogListener(dialogListener: OnMarkerDialogListener) {
        this.dialogListener = dialogListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = SetMarkerDialogBinding.inflate(inflater, container, false)
        isCancelable = false

        binding.errorMessageDialog.visibility = View.GONE
        binding.titleDialog.text = title
        binding.messageDialog.text = message
        binding.positiveButtonDialog.text = title
        binding.editTextDialog.visibility = editTextVisibility
        binding.positiveButtonDialog.setOnClickListener {
            if (dialogListener.setPositiveButton(binding, latLng, marker))
                dismiss()
        }
        binding.negativeButtonDialog.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}