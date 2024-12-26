package com.example.alarmclock

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alarmclock.databinding.ActivityMainBinding
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import java.time.LocalTime

class MainActivity : AppCompatActivity() {
    private lateinit var timesFlow: Flow<List<LocalTime>>
    private val alarmScheduler = AlarmScheduler(this)
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        timesFlow = this.timesDataStore.data
            .map { times ->
                times.timesSetList.map { LocalTime.ofSecondOfDay(it) }
            }.catch {
                makeToast(R.string.error_reading_alarms)
                emit(emptyList())
            }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val adapter = TimesAdapter(getString(R.string.time_title)) { index ->
            lifecycleScope.launch {
                deleteTime(index)
            }
        }
        binding.apply {
            listRV.layoutManager = LinearLayoutManager(this@MainActivity)
            listRV.adapter = adapter
            addBTN.setOnClickListener {
                val now = LocalTime.now()
                val picker = MaterialTimePicker.Builder()
                    .setTitleText(getString(R.string.select_time))
                    .setHour(now.hour)
                    .setMinute(now.minute)
                    .build()
                picker.addOnPositiveButtonClickListener {
                    lifecycleScope.launch {
                        addTime(LocalTime.of(picker.hour, picker.minute))
                    }
                }
                picker.show(supportFragmentManager, "TimePicker")
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                timesFlow.collect { times ->
                    alarmScheduler.scheduleNextAlarm(times)
                    adapter.submitList(times)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_exit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_exit -> {
                moveTaskToBack(true)
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    suspend fun addTime(time: LocalTime) {
        this.timesDataStore.updateData { current ->
            if (!current.timesSetList.contains(time.toSecondOfDay().toLong())) {
                current.toBuilder().addTimesSet(time.toSecondOfDay().toLong()).build()
            } else {
                makeToast(R.string.already_in_list)
                current
            }
        }
    }

    suspend fun deleteTime(index: Int) {
        this.timesDataStore.updateData { current ->
            val list = current.timesSetList.toMutableList()
            list.removeAt(index)
            current.toBuilder().clearTimesSet().addAllTimesSet(list).build()
        }
    }
}