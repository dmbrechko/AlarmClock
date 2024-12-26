package com.example.alarmclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.LocalTime

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val ringtone = RingtoneManager
            .getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            ?: RingtoneManager
                .getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
        ringtone?.play()
        val scheduler = AlarmScheduler(context)
        val times = try {
            runBlocking { context.timesDataStore.data.first() }.timesSetList.map {
                LocalTime.ofSecondOfDay(it)
            }
        } catch (e: Exception) { emptyList<LocalTime>() }
        scheduler.scheduleNextAlarm(times)
        val activityIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(activityIntent)
    }
}