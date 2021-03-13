package com.katyrin.movieapp.view

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.katyrin.movieapp.R
import com.katyrin.movieapp.databinding.FragmentMapBinding
import com.katyrin.movieapp.databinding.SetMarkerDialogBinding
import com.katyrin.movieapp.model.Cinema
import com.katyrin.movieapp.viewmodel.MapsViewModel
import java.io.IOException


private const val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002

class MapsFragment : Fragment() {

    companion object {
        val TAG: String = MapsFragment::class.java.simpleName
        fun newInstance() = MapsFragment()
    }

    private lateinit var map: GoogleMap
    private lateinit var binding: FragmentMapBinding
    private val geofenceList: MutableList<Geofence> = mutableListOf()
    private val geofenceHelper: GeofenceHelper by lazy { GeofenceHelper.newInstance(requireContext()) }
    private val geofencingClient: GeofencingClient by lazy {
        LocationServices.getGeofencingClient(requireContext())
    }
    private val viewModel: MapsViewModel by lazy {
        ViewModelProvider(this).get(MapsViewModel::class.java)
    }
    private val pendingIntent: PendingIntent by lazy { geofenceHelper.getPendingIntentService() }

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val nsk = LatLng(54.98296090807848, 82.89598294067132)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(nsk, 15f))
        googleMap.let {
            it.setOnMapLongClickListener { latLng ->
                onMapLongClick(latLng)
            }
            it.setOnMarkerClickListener { marker ->
                createRemoveMarkerDialog(marker)
                true
            }
            activityMyLocation(it)
        }
    }

    private fun onMapLongClick(latLng: LatLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
                getAddressAsync(latLng)
                createAddMarkerDialog(latLng)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            ActivityCompat.requestPermissions(requireActivity(),
                                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                                BACKGROUND_LOCATION_ACCESS_REQUEST_CODE)
                } else {
                    ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        BACKGROUND_LOCATION_ACCESS_REQUEST_CODE)
                }
            }
        } else {
            getAddressAsync(latLng)
            createAddMarkerDialog(latLng)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        viewModel.getLiveData().observe(viewLifecycleOwner, { renderCinemasData(it) })

        viewModel.getAllCinemas()
        mapFragment?.getMapAsync(callback)
        initSearchByAddress()
        showAlertDialogInfo()
    }

    private fun renderCinemasData(cinemas: List<Cinema>) {
        removeGeofences()
        for (cinema in cinemas) {
            val location = LatLng(cinema.lat, cinema.lng)
            setMarker(location, cinema.placeName)
            addGeofenceToList(location, cinema.placeName)
        }
        addGeofences()
    }

    private fun showAlertDialogInfo() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.choice_of_cinemas))
            .setMessage(getString(R.string.choice_of_cinemas_dialog_message))
            .setPositiveButton(getString(R.string.select_cinemas)) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private val addMarkerDialogListener = object : OnMarkerDialogListener {
        override fun setPositiveButton(dialogBinding: SetMarkerDialogBinding, latLng: LatLng,
                                       marker: Marker?) : Boolean {
            val enterText = dialogBinding.editTextDialog.text.toString()
            return if (enterText == ""){
                dialogBinding.errorMessageDialog.visibility = View.VISIBLE
                dialogBinding.errorMessageDialog.text = getString(R.string.error_dialog_message)
                false
            } else {
                map.clear()
                viewModel.saveCinemaToDB(Cinema(enterText, latLng.latitude, latLng.longitude))
                true
            }
        }
    }

    private val removeMarkerDialogListener = object : OnMarkerDialogListener {
        override fun setPositiveButton(dialogBinding: SetMarkerDialogBinding, latLng: LatLng,
                                       marker: Marker?) : Boolean {
            map.clear()
            marker?.remove()
            marker?.title?.let { viewModel.deleteCinemaToDB(it) }
            return true
        }
    }

    private fun createAddMarkerDialog(latLng: LatLng) {
        val addMarkerDialog = MarkerDialog(
            getString(R.string.add_cinema),
            getString(R.string.add_marker_dialog_message),
            View.VISIBLE,
            latLng
        )
        addMarkerDialog.setOnDialogListener(addMarkerDialogListener)
        addMarkerDialog.show(requireActivity().supportFragmentManager, "AddMarkerDialog")
    }

    private fun createRemoveMarkerDialog( marker: Marker) {
        val removeMarkerDialog = MarkerDialog(
            getString(R.string.remove_cinema),
            marker.title.toString(),
            View.GONE,
            marker = marker
        )
        removeMarkerDialog.setOnDialogListener(removeMarkerDialogListener)
        removeMarkerDialog.show(requireActivity().supportFragmentManager, "RemoveMarkerDialog")
    }

    private fun getAddressAsync(location: LatLng) {
        context?.let {
            val geoCoder = Geocoder(it)
            Thread {
                try {
                    val addresses =
                        geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    binding.textAddress.post {
                        binding.textAddress.text = addresses[0].getAddressLine(0)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun setMarker(location: LatLng, searchText: String): Marker {
        return map.addMarker(
            MarkerOptions()
                .position(location)
                .title(searchText)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin))
        )
    }

    private fun initSearchByAddress() {
        binding.buttonSearch.setOnClickListener {
            val geoCoder = Geocoder(it.context)
            val searchText = binding.searchAddress.text.toString()
            Thread {
                try {
                    val addresses = geoCoder.getFromLocationName(searchText, 1)
                    if (addresses.isNotEmpty()) {
                        goToAddress(addresses, searchText)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun goToAddress(addresses: MutableList<Address>, searchText: String) {
        val location = LatLng(addresses[0].latitude, addresses[0].longitude)
        activity?.runOnUiThread {
            setMarker(location, searchText)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
        createAddMarkerDialog(location)
    }

    private fun activityMyLocation(googleMap: GoogleMap) {
        context?.let {
            val isPermissionGranted =
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
            googleMap.isMyLocationEnabled = isPermissionGranted
            googleMap.uiSettings.isMyLocationButtonEnabled = isPermissionGranted
            googleMap.uiSettings.isZoomControlsEnabled = true
        }
    }

    private fun addGeofenceToList(latLng: LatLng, id: String) {
        val geofence: Geofence = geofenceHelper.getGeofence(
            id,
            latLng, 200f,
            Geofence.GEOFENCE_TRANSITION_ENTER
                    or Geofence.GEOFENCE_TRANSITION_DWELL
                    or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        geofenceList.add(geofence)
    }

    private fun removeGeofences() {
        geofenceList.clear()
        geofencingClient.removeGeofences(pendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "onSuccess: Geofence Removed...")
            }
            .addOnFailureListener{ e ->
                val errorMessage: String = geofenceHelper.getErrorString(e)
                Log.d(TAG, "onFailure: $errorMessage")
            }
    }

    private fun addGeofences() {
        if (context?.let {
                ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
        } != PackageManager.PERMISSION_GRANTED) return

        if (geofenceList.isNotEmpty()) {
            val geofencingRequest: GeofencingRequest = geofenceHelper.getGeofencingRequest(geofenceList)
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener {
                    Log.d(TAG, "onSuccess: Geofence Added...")
                }
                .addOnFailureListener{ e ->
                    val errorMessage: String = geofenceHelper.getErrorString(e)
                    Log.d(TAG, "onFailure: $errorMessage")
                }
        }
    }
}