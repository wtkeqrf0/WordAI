package com.sizov.wordai.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sizov.wordai.screens.MainActivity

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val scheduleIntent = Intent(context, MainActivity::class.java)
            scheduleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(scheduleIntent)
        }
    }
}
