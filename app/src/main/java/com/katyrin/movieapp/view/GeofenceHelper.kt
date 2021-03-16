package com.katyrin.movieapp.view

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng
import com.katyrin.movieapp.R


class GeofenceHelper(base: Context) : ContextWrapper(base) {
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun getGeofencingRequest(geofenceList: List<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder()
            .addGeofences(geofenceList)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
    }

    fun getGeofence(ID: String, latLng: LatLng, radius: Float, transitionTypes: Int): Geofence {
        return Geofence.Builder()
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setRequestId(ID)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(5000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    fun getPendingIntentService(): PendingIntent {
        return geofencePendingIntent
    }

    fun getErrorString(e: Exception): String {
        if (e is ApiException) {
            when (e.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE ->
                    return getString(R.string.geofence_not_available)
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES ->
                    return getString(R.string.too_many_geofences)
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS ->
                    return getString(R.string.too_many_pending_intents)
            }
        }
        return e.localizedMessage!!
    }

    companion object {
        fun newInstance(context: Context) = GeofenceHelper(context)
    }
}