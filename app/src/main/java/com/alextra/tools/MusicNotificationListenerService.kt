package com.alextra.tools;

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification;


class MusicNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn:StatusBarNotification) {
        val notificationPackageName = sbn.packageName
        val notificationTitle = sbn.notification?.extras?.getString("android.title")
        val notificationText = sbn.notification?.extras?.getString("android.text")

        if (notificationTitle != null && notificationText != null) {
            // Check if the notification contains music-related keywords or app package names
            val isPlayingMusic = isMusicNotification(notificationPackageName, notificationTitle, notificationText)

            if (isPlayingMusic) {
                // Handle the case where music is playing
                // You can send a broadcast, update UI, or perform other actions here
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        if (sbn == null) return;
        val notificationPackageName = sbn.packageName
        val notificationTitle = sbn.notification?.extras?.getString("android.title")
        val notificationText = sbn.notification?.extras?.getString("android.text")

        if (notificationTitle != null && notificationText != null) {
            // Check if the notification was removed from a music-related app
            val isMusicNotification = isMusicNotification(notificationPackageName, notificationTitle, notificationText)

            if (isMusicNotification) {
                // Handle the case where music playback has stopped or changed
                // You can send a broadcast, update UI, or perform other actions here
            }
        }
    }

    private fun isMusicNotification(packageName: String, title: String, text: String): Boolean {
        val musicKeywords = listOf("music", "song", "audio", "now playing")
        val musicAppPackages = listOf("com.spotify.music", "com.apple.android.music", "com.google.android.music")

        return (musicKeywords.any { title.contains(it, ignoreCase = true) } ||
        musicKeywords.any { text.contains(it, ignoreCase = true) } ||
        musicAppPackages.contains(packageName))
    }

}
