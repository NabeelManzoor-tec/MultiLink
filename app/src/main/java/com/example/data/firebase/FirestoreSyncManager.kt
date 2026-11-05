package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.ClickEvent
import com.example.data.model.CreatorProfile
import com.example.data.model.LinkCategory
import com.example.data.model.LinkItem
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

object FirestoreSyncManager {
    private const val TAG = "FirestoreSync"
    private var eventListenerRegistration: ListenerRegistration? = null

    fun getFirestore(context: Context): FirebaseFirestore {
        val dbId = context.getString(R.string.firestore_database_id)
        return FirebaseFirestore.getInstance(dbId)
    }

    fun cleanHandle(handle: String): String {
        return handle.trim().removePrefix("@").lowercase().ifEmpty { "alexa_aura" }
    }

    suspend fun syncProfileToFirestore(context: Context, profile: CreatorProfile) {
        try {
            val db = getFirestore(context)
            val handle = cleanHandle(profile.handle)
            val docRef = db.collection("public_profiles").document(handle)

            val data = hashMapOf<String, Any>(
                "handle" to profile.handle,
                "cleanHandle" to handle,
                "displayName" to profile.displayName,
                "headline" to profile.headline,
                "bio" to profile.bio,
                "avatarDrawableName" to profile.avatarDrawableName,
                "location" to profile.location,
                "isVerified" to profile.isVerified,
                "isProUser" to profile.isProUser,
                "proPlanTier" to profile.proPlanTier,
                "themeId" to profile.themeId,
                "customDomain" to profile.customDomain,
                "tipGoalCurrent" to profile.tipGoalCurrent,
                "tipGoalTarget" to profile.tipGoalTarget,
                "tipGoalTitle" to profile.tipGoalTitle,
                "preferredTipMethod" to profile.preferredTipMethod,
                "stripeConnected" to profile.stripeConnected,
                "paypalHandle" to profile.paypalHandle,
                "cashAppTag" to profile.cashAppTag,
                "buyMeACoffeeUrl" to profile.buyMeACoffeeUrl,
                "lastUpdated" to FieldValue.serverTimestamp()
            )

            docRef.set(data).await()
            Log.d(TAG, "Profile successfully synced to public_profiles/$handle")
        } catch (e: Exception) {
            Log.w(TAG, "Error syncing profile to Firestore: ${e.message}")
        }
    }

    suspend fun syncLinksToFirestore(context: Context, handleRaw: String, links: List<LinkItem>) {
        try {
            val db = getFirestore(context)
            val handle = cleanHandle(handleRaw)
            val linksCollection = db.collection("public_profiles").document(handle).collection("links")

            for (link in links) {
                val linkData = hashMapOf<String, Any>(
                    "id" to link.id,
                    "title" to link.title,
                    "url" to link.url,
                    "category" to link.category.name,
                    "iconKey" to link.iconKey,
                    "isActive" to link.isActive,
                    "price" to link.price,
                    "subtitle" to link.subtitle,
                    "badge" to link.badge,
                    "coverImageName" to link.coverImageName,
                    "isFeatured" to link.isFeatured,
                    "orderIndex" to link.orderIndex,
                    "totalClicks" to link.totalClicks,
                    "downloadFileName" to link.downloadFileName,
                    "fileSizeBytes" to link.fileSizeBytes,
                    "licenseType" to link.licenseType
                )
                linksCollection.document(link.id.toString()).set(linkData).await()
            }
            Log.d(TAG, "Synced ${links.size} links to public_profiles/$handle/links")
        } catch (e: Exception) {
            Log.w(TAG, "Error syncing links to Firestore: ${e.message}")
        }
    }

    suspend fun deleteLinkFromFirestore(context: Context, handleRaw: String, linkId: Long) {
        try {
            val db = getFirestore(context)
            val handle = cleanHandle(handleRaw)
            db.collection("public_profiles").document(handle)
                .collection("links").document(linkId.toString())
                .delete().await()
        } catch (e: Exception) {
            Log.w(TAG, "Error deleting link from Firestore: ${e.message}")
        }
    }

    fun startWebEventListener(
        context: Context,
        handleRaw: String,
        onNewEvent: (ClickEvent) -> Unit
    ) {
        val handle = cleanHandle(handleRaw)
        eventListenerRegistration?.remove()

        try {
            val db = getFirestore(context)
            val eventsCollection = db.collection("public_profiles").document(handle).collection("events")

            val startTime = System.currentTimeMillis() - 5000L
            eventListenerRegistration = eventsCollection
                .whereGreaterThanOrEqualTo("timestamp", startTime)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "Event listener error: ${e.message}")
                        return@addSnapshotListener
                    }

                    snapshots?.documentChanges?.forEach { change ->
                        if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                            val doc = change.document
                            val linkId = doc.getLong("linkId") ?: 0L
                            val linkTitle = doc.getString("linkTitle") ?: "Web Visit"
                            val category = doc.getString("category") ?: LinkCategory.SOCIAL.name
                            val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                            val source = doc.getString("sourcePlatform") ?: "Public Web Version"
                            val country = doc.getString("country") ?: "Global Web"
                            val device = doc.getString("device") ?: "Desktop Browser"
                            val rev = doc.getDouble("revenueAmount") ?: 0.0

                            val event = ClickEvent(
                                linkId = linkId,
                                linkTitle = linkTitle,
                                category = category,
                                timestamp = timestamp,
                                sourcePlatform = source,
                                country = country,
                                device = device,
                                revenueAmount = rev
                            )
                            onNewEvent(event)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to start Firestore web event listener: ${e.message}")
        }
    }

    fun stopWebEventListener() {
        eventListenerRegistration?.remove()
        eventListenerRegistration = null
    }
}
