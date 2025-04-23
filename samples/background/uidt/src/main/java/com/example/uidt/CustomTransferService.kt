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

package com.example.uidt

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Transfer service to define the data transfer and behavior for [onStartJob] and [onStopJob].
 */
class CustomTransferService : JobService() {

    private lateinit var notificationManager: CustomTransferNotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = CustomTransferNotificationManager(applicationContext)
        notificationManager.createNotificationChannel()
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onStartJob(params: JobParameters): Boolean {
        val notification = notificationManager.createNotification(
            "User-initiated data transfer job",
            "Job is running",
        )

        setNotification(
            params,
            CustomTransferNotificationManager.NOTIFICATION_ID,
            notification,
            JOB_END_NOTIFICATION_POLICY_DETACH,
        )
        // Execute the work associated with this job asynchronously.
        scope.launch {
            doDownload(params)
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun doDownload(params: JobParameters) {
        // Simulate a download by delaying for a few seconds.
        // In a real app, you would perform the actual download here.
        val simulatedDownloadSize = 50 // Representing, e.g., 50 "chunks" of data
        for (i in 1..simulatedDownloadSize) {
            // Simulate progress updates (optional, but good for user feedback)
            val progress = (i * 100) / simulatedDownloadSize
            notificationManager.updateNotification(
                CustomTransferNotificationManager.NOTIFICATION_ID,
                "Downloading...",
                "Progress: $progress%",
            )
            delay(500)

            notificationManager.updateNotification(
                CustomTransferNotificationManager.NOTIFICATION_ID,
                "Download Complete", "Download successful",
            )
            // Run the relevant async download task, then call
            // jobFinished once the task is completed.
            jobFinished(params, false)
        }
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }


}