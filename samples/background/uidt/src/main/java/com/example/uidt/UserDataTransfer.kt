package com.example.uidt

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_NOT_METERED
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.uidt.ui.theme.PlatformSamplesTheme


class UserDataTransferActivity : ComponentActivity() {

    //The notification permission is needed to inform the user that the transfer is in progress
    private val notificationPermission = "android.permission.POST_NOTIFICATIONS"

    private var showPermissionDeniedDialog by mutableStateOf(false)
    private var buttonClicked by mutableStateOf(false)


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                userInitiatedJob()
            } else {
                showPermissionDeniedDialog = true
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlatformSamplesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        //UIDT requires Android 14 or higher, inform user if they are using an older
                        // version
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            IncompatibleVersion()
                        } else {
                            StartTransferButton(
                                onClick = {
                                    checkAndRequestNotificationPermission()
                                    buttonClicked = true
                                },

                                )
                            if (buttonClicked) {
                                Text("Check notifications to monitor progress")

                            }
                        }
                    }
                    //If the notification permission is denied, show the dialog
                    if (showPermissionDeniedDialog) {
                        PermissionDeniedDialog(
                            onDismiss = {
                                showPermissionDeniedDialog = false
                            },
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun checkAndRequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                notificationPermission,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            userInitiatedJob()
        } else {
            requestPermissionLauncher.launch(notificationPermission)
        }

    }

    //Create the UIDT job
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun userInitiatedJob() {
        val networkRequestBuilder = NetworkRequest.Builder()
            // Add or remove capabilities based on your requirements.
            // For example, this code specifies that the job won't run
            // unless there's a connection to the internet (not just a local
            // network), and the connection doesn't charge per-byte.
            .addCapability(NET_CAPABILITY_INTERNET).addCapability(NET_CAPABILITY_NOT_METERED)
            .build()


        val jobInfo = JobInfo.Builder(1, ComponentName(this, CustomTransferService::class.java))
            // Indicates the job is fulfilling a user request
            .setUserInitiated(true).setRequiredNetwork(networkRequestBuilder)
            // Provide your estimate of the network traffic here
            .setEstimatedNetworkBytes(1024 * 1024 * 1024, 1024 * 1024 * 1024)
            // ...
            .build()

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(jobInfo)
    }
}

//Dialog to inform the user that the permission is required
@Composable
fun PermissionDeniedDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Permission Required")
        },
        text = {
            Text("This feature requires the notification permission. Please grant it in settings.")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        },
    )
}

//Button for initiating transfer
@Composable
fun StartTransferButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = "Run User-Initiated Data Transfer")
    }
}

@Composable
fun IncompatibleVersion() {
    Text("Incompatible version: UIDT requires Android 14 or higher.")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlatformSamplesTheme {
        StartTransferButton(onClick = {})
    }
}