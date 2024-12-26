package com.example.alarmclock

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.alarmclock.databinding.ListItemBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TimesAdapter(private val str: String, private val onDelete: (Int) -> Unit):
    ListAdapter<LocalTime, TimesAdapter.TimeViewHolder>(DiffUtilCallback()) {
    private val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeViewHolder(binding, str, formatter, onDelete)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TimeViewHolder(
        val binding: ListItemBinding,
        val str: String,
        val formatter: DateTimeFormatter,
        onDelete: (Int) -> Unit): ViewHolder(binding.root) {
        init {
            binding.deleteIV.setOnClickListener {
                onDelete(adapterPosition)
            }
        }

        fun bind(time: LocalTime) {
            val strTime = formatter.format(time)
            binding.titleTV.text = String.format(str, adapterPosition + 1, strTime)
        }
    }
}

class DiffUtilCallback: DiffUtil.ItemCallback<LocalTime>() {
    override fun areItemsTheSame(oldItem: LocalTime, newItem: LocalTime): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: LocalTime, newItem: LocalTime): Boolean {
        return oldItem == newItem // because its data class
    }
}