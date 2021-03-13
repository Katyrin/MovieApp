package com.katyrin.movieapp.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent != null) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            if (geofencingEvent.hasError()) {
                Log.d(TAG, "onReceive: Error receiving geofence event...")
                return
            }
            val geofenceList = geofencingEvent.triggeringGeofences
            for (geofence in geofenceList) {
                Log.d(TAG, "onReceive: " + geofence.requestId)
            }
            when (geofencingEvent.geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> {
                    Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "GEOFENCE_TRANSITION_ENTER")
                }
                Geofence.GEOFENCE_TRANSITION_DWELL -> {
                    Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "GEOFENCE_TRANSITION_DWELL")
                }
                Geofence.GEOFENCE_TRANSITION_EXIT -> {
                    Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "GEOFENCE_TRANSITION_EXIT")
                }
            }
        } else {
            Log.d(TAG, "intent is equals null")
        }
    }

    companion object {
        private const val TAG = "GeofenceBroadcastReceiv"
    }
}