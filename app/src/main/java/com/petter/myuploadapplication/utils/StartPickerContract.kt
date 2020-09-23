package com.petter.myuploadapplication.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract

class StartPickerContract(private val intent: Intent) : ActivityResultContract<Int, Intent?>() {
    override fun createIntent(context: Context, input: Int?): Intent = intent

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
        return when (resultCode) {
            Activity.RESULT_OK -> intent
            else -> null
        }
    }
}