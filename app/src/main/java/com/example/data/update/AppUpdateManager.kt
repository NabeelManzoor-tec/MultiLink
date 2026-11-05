package com.example.data.update

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.firebase.FirestoreSyncManager
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AppUpdateData(
    val latestVersionCode: Int,
    val latestVersionName: String,
    val releaseNotes: String,
    val isMandatory: Boolean = false,
    val appUpdateInfo: AppUpdateInfo? = null
)

sealed class AppUpdateState {
    object Idle : AppUpdateState()
    object Checking : AppUpdateState()
    object UpToDate : AppUpdateState()
    data class Available(val updateData: AppUpdateData) : AppUpdateState()
    data class Error(val message: String) : AppUpdateState()
}

class AppUpdateManager(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    companion object {
        private const val TAG = "AppUpdateManager"
        const val CHANNEL_ID = "multilink_app_updates"
        const val NOTIFICATION_ID = 5001
        const val PLAY_STORE_PACKAGE = "com.aistudio.multilink.kzqvw"
    }

    private val playAppUpdateManager = AppUpdateManagerFactory.create(context)

    private val _updateState = MutableStateFlow<AppUpdateState>(AppUpdateState.Idle)
    val updateState: StateFlow<AppUpdateState> = _updateState.asStateFlow()

    private val _isUpdateDialogVisible = MutableStateFlow(false)
    val isUpdateDialogVisible: StateFlow<Boolean> = _isUpdateDialogVisible.asStateFlow()

    init {
        createNotificationChannel()
    }

    val currentVersionCode: Int by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                ).longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0).versionCode
            }
        } catch (e: Exception) {
            5 // Default fallback from build.gradle.kts
        }
    }

    val currentVersionName: String by lazy {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "5.0"
        } catch (e: Exception) {
            "5.0"
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "App Updates"
            val descriptionText = "Alerts when a new version of MultiLink is available"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun checkForUpdates(isUserInitiated: Boolean = false) {
        scope.launch {
            _updateState.value = AppUpdateState.Checking

            // Step 1: Check Google Play Store In-App Updates API
            try {
                val appUpdateInfo = playAppUpdateManager.appUpdateInfo.await()
                val isAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                        appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS

                if (isAvailable) {
                    val targetCode = appUpdateInfo.availableVersionCode()
                    val targetName = "${targetCode}.0"
                    val data = AppUpdateData(
                        latestVersionCode = targetCode,
                        latestVersionName = targetName,
                        releaseNotes = "New features, performance enhancements, and bug fixes now available.",
                        isMandatory = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE),
                        appUpdateInfo = appUpdateInfo
                    )
                    _updateState.value = AppUpdateState.Available(data)
                    _isUpdateDialogVisible.value = true
                    sendSystemNotification(data)
                    return@launch
                }
            } catch (e: Exception) {
                Log.d(TAG, "Play Store in-app update check note: ${e.message}")
            }

            // Step 2: Check Cloud Firestore remote release configuration
            try {
                val db = FirestoreSyncManager.getFirestore(context)
                val releaseDoc = db.collection("system_config").document("app_release").get().await()

                if (releaseDoc.exists()) {
                    val remoteVersionCode = (releaseDoc.getLong("latestVersionCode") ?: 0L).toInt()
                    val remoteVersionName = releaseDoc.getString("latestVersionName") ?: "${remoteVersionCode}.0"
                    val notes = releaseDoc.getString("releaseNotes")
                        ?: "Improved performance, updated in-app monetization, and critical stability updates."
                    val mandatory = releaseDoc.getBoolean("isMandatory") ?: false

                    if (remoteVersionCode > currentVersionCode) {
                        val data = AppUpdateData(
                            latestVersionCode = remoteVersionCode,
                            latestVersionName = remoteVersionName,
                            releaseNotes = notes,
                            isMandatory = mandatory
                        )
                        _updateState.value = AppUpdateState.Available(data)
                        _isUpdateDialogVisible.value = true
                        sendSystemNotification(data)
                        return@launch
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Remote Firestore update check note: ${e.message}")
            }

            // If no update detected
            _updateState.value = AppUpdateState.UpToDate
            if (isUserInitiated) {
                _isUpdateDialogVisible.value = false
            }
        }
    }

    fun dismissUpdateDialog() {
        _isUpdateDialogVisible.value = false
    }

    fun showUpdateDialog() {
        _isUpdateDialogVisible.value = true
    }

    fun sendSystemNotification(data: AppUpdateData) {
        try {
            val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$PLAY_STORE_PACKAGE")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                playIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("MultiLink v${data.latestVersionName} Available! 🚀")
                .setContentText("A new update is now available on Google Play. Tap to update.")
                .setStyle(NotificationCompat.BigTextStyle().bigText(
                    "MultiLink version ${data.latestVersionName} is live on Google Play!\n\n${data.releaseNotes}"
                ))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
            notificationManager?.notify(NOTIFICATION_ID, builder.build())
        } catch (e: Exception) {
            Log.w(TAG, "Could not send notification: ${e.message}")
        }
    }

    fun launchPlayStoreUpdate(activity: Activity? = null) {
        val currentState = _updateState.value
        if (currentState is AppUpdateState.Available && currentState.updateData.appUpdateInfo != null && activity != null) {
            val info = currentState.updateData.appUpdateInfo
            try {
                val updateType = if (info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) AppUpdateType.FLEXIBLE else AppUpdateType.IMMEDIATE
                val options = AppUpdateOptions.newBuilder(updateType).build()
                playAppUpdateManager.startUpdateFlow(info, activity, options)
                return
            } catch (e: Exception) {
                Log.w(TAG, "Play in-app update flow fallback: ${e.message}")
            }
        }

        // Direct Google Play Store app fallback
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$PLAY_STORE_PACKAGE")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$PLAY_STORE_PACKAGE")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }
}
