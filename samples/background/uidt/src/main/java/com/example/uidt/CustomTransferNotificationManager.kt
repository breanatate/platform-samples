/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.uidt;

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context;
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService

/**
 * To manage notifications for [CustomTransferService].
 */

class CustomTransferNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "DataTransferChannel"
        const val NOTIFICATION_ID = 1 // Unique ID for the notification
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotification(title: String, message: String): Notification {
        return Notification.Builder(context, CHANNEL_ID) // Access CHANNEL_ID directly
            .setContentTitle(title).setContentText(message)
            .setSmallIcon(android.R.mipmap.sym_def_app_icon).build()
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "DataTransferChannel"
            val name = "Data Transfer Notifications" // User-visible name of the channel.
            val descriptionText =
                "Notifications for data transfer progress." // User-visible description of the channel.
            val importance = NotificationManager.IMPORTANCE_DEFAULT // Importance level
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            getSystemService(context, NotificationManager::class.java)?.createNotificationChannel(
                channel,
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateNotification(notificationId: Int, s: String, s1: String) {
        val notification = createNotification(s, s1)
        getSystemService(context, NotificationManager::class.java)?.notify(
            notificationId,
            notification,
        )

    }


}
