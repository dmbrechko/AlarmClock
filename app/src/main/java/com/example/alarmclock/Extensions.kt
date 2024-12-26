package com.example.alarmclock

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.makeToast(@StringRes string: Int) {
    Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
}

fun Context.makeToast(string: String) {
    Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
}