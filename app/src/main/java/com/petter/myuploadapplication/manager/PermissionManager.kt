package com.petter.myuploadapplication.manager

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class PermissionManager(
    private val activity: ComponentActivity,
    private val onDeny: () -> Unit = {},
    private val onRationale: () -> Unit = {}
) {
    private var onGranted: () -> Unit = {}


    fun requestPermission(permission: String, onGranted: () -> Unit) {
        this.onGranted = onGranted
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> onGranted()
                activity.shouldShowRequestPermissionRationale(permission) -> onRationale()
                else -> onDeny()
            }
        }.launch(permission)
    }

    fun requestPermissions(vararg required: String, onGranted: () -> Unit) {
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            var isGranted = false
            map.entries.forEach {
                isGranted = it.value
            }
            if (isGranted) onGranted() else onDeny()
        }.launch(required)
    }
}