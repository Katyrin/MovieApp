package com.katyrin.movieapp.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.katyrin.movieapp.R


class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            if (geofencingEvent.hasError()) {
                context?.getString(R.string.on_receive_error)?.let { Log.d(TAG, it) }
                return
            }
            val geofenceList = geofencingEvent.triggeringGeofences
            for (geofence in geofenceList) {
                when (geofencingEvent.geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        context?.getString(R.string.notification_message)?.let {
                            showNotification("${context.getString(R.string.near)} ${geofence.requestId}",
                                it, context)
                        }
                        context?.getString(R.string.geofence_transition_enter)?.let { Log.d(TAG, it) }
                    }
                }
                Log.d(TAG, "${context?.getString(R.string.on_receive)} ${geofence.requestId}")
            }
        } else {
            context?.getString(R.string.intent_null)?.let { Log.d(TAG, it) }
        }
    }

    private fun showNotification(title: String, message: String, context: Context?) {
        val notificationBuilder =
            context?.let {
                NotificationCompat.Builder(it, CHANNEL_ID).apply {
                    setSmallIcon(R.drawable.ic_map_marker)
                    setContentTitle(title)
                    setContentText(message)
                    setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    priority = NotificationCompat.PRIORITY_HIGH
                }
            }
        val notificationManager: NotificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager, context)
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder?.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager, context: Context?) {
        val name = context?.getString(R.string.geofence)
        val descriptionText = context?.getString(R.string.geofence_notification)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val TAG = "GeofenceBroadcast"
        private const val CHANNEL_ID = "Geofence ID"
        private const val NOTIFICATION_ID = 24
    }
}