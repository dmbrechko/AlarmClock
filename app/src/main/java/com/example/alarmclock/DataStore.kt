package com.example.alarmclock

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

val Context.timesDataStore: DataStore<TimeOfAlarm.TimesOfAlarm> by dataStore(
    fileName = "time_of_alarm.pb",
    serializer = TimesSerializer
)

object TimesSerializer: Serializer<TimeOfAlarm.TimesOfAlarm> {
    override val defaultValue: TimeOfAlarm.TimesOfAlarm
        get() = TimeOfAlarm.TimesOfAlarm.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): TimeOfAlarm.TimesOfAlarm {
        return try {
            TimeOfAlarm.TimesOfAlarm.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: TimeOfAlarm.TimesOfAlarm, output: OutputStream) {
        t.writeTo(output)
    }

}