package com.example.alarmclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalTime
import java.time.ZonedDateTime

class AlarmScheduler(val context: Context) {
    private fun calculateNextAlarm(times: List<LocalTime>): ZonedDateTime {
        check(times.isNotEmpty())
        val now = ZonedDateTime.now()
        val sortedTimes = times.sorted()
        val nextTime = sortedTimes.firstOrNull { it > now.toLocalTime() }
            ?: times.firstOrNull() ?: throw IllegalStateException("Wrong checks earlier")
        return if (now.toLocalTime() < nextTime) {
            now.with(nextTime)
        } else {
            now.plusDays(1).with(nextTime)
        }
    }
    fun scheduleNextAlarm(times: List<LocalTime>){
        if (times.isEmpty()) return
        val nextTime = calculateNextAlarm(times)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val pi = PendingIntent.getBroadcast(context, CODE_PENDING_ALARM, intent, flags)
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTime.toEpochSecond() * 1000, pi)
    }

    companion object {
        const val CODE_PENDING_ALARM = 31 * 17
    }
}