package com.example.creditcardrecommender.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.creditcardrecommender.data.db.AppDatabase
import com.example.creditcardrecommender.notification.NotificationHelper
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null || geofencingEvent.hasError()) {
            Log.e("GeofenceReceiver", "Error receiving geofence event")
            return
        }

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            triggeringGeofences?.forEach { geofence ->
                val locationId = geofence.requestId.toIntOrNull()
                if (locationId != null) {
                    processGeofenceEntry(context, locationId)
                }
            }
        }
    }

    private fun processGeofenceEntry(context: Context, locationId: Int) {
        val db = AppDatabase.getDatabase(context)
        val dao = db.appDao()
        val notificationHelper = NotificationHelper(context)

        CoroutineScope(Dispatchers.IO).launch {
            val location = dao.getLocationById(locationId)
            location?.categoryId?.let { categoryId ->
                val bestCard = dao.getBestCardForCategory(categoryId)
                if (bestCard != null) {
                    notificationHelper.showRecommendationNotification(
                        title = "You're at ${location.name}!",
                        message = "Use your ${bestCard.name} for the best rewards here."
                    )
                }
            }
        }
    }
}
