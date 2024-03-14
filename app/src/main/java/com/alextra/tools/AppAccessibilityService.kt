package com.alextra.tools;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.Nullable;

class AppAccessibilityService : AccessibilityService() {

    val storage : Storage = Storage.getInstance(this);

    override fun onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            // The event package name represents the app package name
            var packageName = event.getPackageName();

            // You can perform actions based on the detected app start

            val action = storage.getString(packageName, null)
            if(action == null){
                return
            }
            else {
                startUserActivity(action)
            }

            if (packageName != null) {
                when (packageName) {
                    "com.example.app1" -> {
                        // React to the start of "app1"
                        // Replace "com.example.app1" with the package name of the app you want to detect
                    }
                    "com.example.app2" -> {
                        // React to the start of "app2"
                        // Replace "com.example.app2" with the package name of another app
                    }
                    // Add more app-specific actions as needed
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;


    }
}
